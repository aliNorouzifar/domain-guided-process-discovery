from dash import html, dcc, dash_table
import dash_bootstrap_components as dbc
import dash_uploader as du
import json
import pandas as pd
import dash_interactive_graphviz
from prolysis.util.redis_connection import redis_client
import dash_ag_grid as dag
from dash_chat import ChatComponent
from enum import Enum


columnDefs = [
    {"field": "rule"},
    {"field": "support"},
    {"field": "confidence"},
]

class AIProviders(Enum):
    GOOGLE = "Google"
    OPENAI = "OpenAI"
    DEEPSEEK = "DeepSeek"
    ANTHROPIC = "Anthropic"
    DEEPINFRA = "Deepinfra"
    MISTRAL_AI = "Mistral AI"
    OPENROUTER = "OpenRouter"
    COHERE = "Cohere"
    GROK = "Grok"

AI_MODEL_DEFAULTS = {
    AIProviders.GOOGLE.value: "gemini-2.5-flash",
    AIProviders.OPENAI.value: "gpt-4.1",
    AIProviders.DEEPSEEK.value: "deepseek-reasoner",
    AIProviders.ANTHROPIC.value: "claude-3-7-sonnet-latest",
    AIProviders.DEEPINFRA.value: "meta-llama/Llama-3.2-90B-Vision-Instruct",
    AIProviders.MISTRAL_AI.value: "mistral-large-latest",
    AIProviders.OPENROUTER.value: "mistralai/devstral-small:free",
    AIProviders.COHERE.value: "command-r-plus",
    AIProviders.GROK.value: "grok-3",
}


def create_layout():
    return dbc.Container(
        className="page-container",
        children=[
            # Navigation Links
            html.Div(
                className="nav-links",
                children=[
                    dcc.Link("Introduction", href="/", className="nav-link"),
                    dcc.Link("IMr", href="/main", className="nav-link"),
                    dcc.Link("LLM", href="/LLM", className="nav-link"),
                    dcc.Link("About Me", href="/about_me", className="nav-link"),
                ],
            ),
            # Tool Name and Description
            html.Div(
                className="tool-name-container",
                children=[
                    html.H1("Rule-guided Process Discovery", className="tool-name"),
                    # html.P(
                    #     "A cutting-edge tool for discovering process models from event logs considering rules as input.",
                    #     className="tool-subtitle",
                    # ),
                ],
            ),
            # Main Content Area
            html.Div(
                className="flex-container",
                children=[
                    # Left Panel: Parameter Settings
                    create_left_panel(),
                    # Right Panel: Visualization Blocks
                    create_right_panel(),
                ],
            ),
        ],
    )


def create_left_panel():
    return html.Div(
        id="left-panel",
        className="left-panel-container",
        children=[
            # html.Div("please select your desired LLM model and provide an API key to enable the assitant!"),
            html.Div(
                style={
                    "border": "1px solid #ccc",
                    "padding": "20px",
                    "borderRadius": "8px",
                    "backgroundColor": "#f9f9f9",
                    "marginBottom": "20px"
                },
                children=[
                    html.H4("LLM Initialization", style={"marginBottom": "15px"}),

                    html.Label("Select LLM Provider:"),
                    dcc.Dropdown(
                        id="llm-provider",
                        options=[{"label": provider.value, "value": provider.value} for provider in AIProviders],
                        placeholder="Choose an LLM provider...",
                        style={"marginBottom": "20px"},
                    ),

                    html.Div(
                        style={"display": "flex", "gap": "20px"},
                        children=[
                            html.Div([
                                html.Label("Model Name:"),
                                dcc.Input(
                                    id="model-name",
                                    type="text",
                                    placeholder="Enter model name...",
                                    style={"width": "100%"},
                                )
                            ], style={"flex": 1}),

                            html.Div([
                                html.Label("API Key:"),
                                dcc.Input(
                                    id="api-key",
                                    type="password",
                                    placeholder="Enter API key...",
                                    style={"width": "100%"},
                                )
                            ], style={"flex": 1}),
                        ]
                    ),

                    html.Div(
                        html.Button("Initialize LLM", id="init-llm-btn", n_clicks=0),
                        style={"marginTop": "20px", "textAlign": "center"}
                    ),
                ]
            ),
            html.Div(id="llm-init-output", style={"marginTop": "10px", "color": "green"}),
            # Header for Parameters Settings
            html.Div(
                className="panel-header",
                children=html.H4("Parameters Settings", className="panel-title"),
            ),
            # Upload Section
            html.Div(
                className="upload-container",
                children=[
                    html.Div(
                        className="section-header",
                        children=html.H4("Upload the Event Log", className="section-title"),
                    ),
                    get_upload_component("upload-data"),
                    html.Br(),
                    # html.Div(
                    #     className="section-header",
                    #     children=html.H4("Do you have prior domain knowledge?", className="section-title"),
                    # ),
                    # dcc.RadioItems(
                    #     id="domain-knowledge-radio",
                    #     options=[
                    #         {"label": "Yes", "value": "yes"},
                    #         {"label": "No", "value": "no"},
                    #     ],
                    #     value="no",  # default
                    #     labelStyle={"display": "inline-block", "marginRight": "15px"},
                    #     inputStyle={"marginRight": "5px"},
                    # ),
                    html.Div(children=
                    [
                        dag.AgGrid(
                            id="rule-table",
                            columnDefs=columnDefs,
                            # rowData=df.to_dict("records"),
                            columnSize="sizeToFit",
                            defaultColDef={
                                "filter": True,
                                "checkboxSelection": {
                                    "function": 'params.column == params.columnApi.getAllDisplayedColumns()[0]'
                                },
                                "headerCheckboxSelection": {
                                    "function": 'params.column == params.columnApi.getAllDisplayedColumns()[0]'
                                }
                            },
                            dashGridOptions={
                                "rowSelection": "multiple",
                                "suppressRowClickSelection": True,
                                "animateRows": False
                            },
                        )
                    ],
                    ),
                    html.Br(),
                    html.Div(id="output-data-upload2"),
                    html.Br(),
                    html.Button("re-discover", id="rediscover-button", n_clicks=0, style={"marginTop": "10px"})
                ],
            ),
        ],
    )


def create_right_panel():
    return html.Div(
        id="right-panel",
        className="right-panel-container",
        children=[
            html.Div(
                className="panel-header",
                children=html.H4("Visualizations and Reports", className="panel-title"),
            ),
            html.Div(
                className="visualization-wrapper",
                children=[
                    html.Div(
                        id="petri_net1_LLM",
                        style={
                            "height": "350px",  # or any fixed size
                            "border": "1px solid #ccc",
                            "backgroundColor": "#f0f0f0",
                            "display": "flex",
                            "justifyContent": "center",
                            "alignItems": "center",
                            "textAlign": "center",
                            "color": "#888"
                        },
                        children="The process model will appear here."
                    )
                ]
            ),
            html.Hr(),
            html.Div(
                className="chat_box",
                id="chat-box-wrapper",
                # style={"display": "none"},  # Initially hidden
                children=[
                    html.Div(
                        id="chat-placeholder",
                        style={
                            "height": "200px",
                            "border": "1px solid #ccc",
                            "backgroundColor": "#f0f0f0",
                            "display": "flex",
                            "justifyContent": "center",
                            "alignItems": "center",
                            "textAlign": "center",
                            "color": "#888",
                            "marginBottom": "20px"
                        },
                        children="Upload an event log to start interacting with the assistant."
                    ),
                    # html.Div([
                    #     ChatComponent(
                    #         id="chat-component",
                    #         messages=[],
                    #     )
                    # ])
                ]
            ),
                    html.Div(id="petri_net2_LLM")
        ]
        )

def get_upload_component(id):
    return du.Upload(
        id=id,
        max_file_size=800,
        chunk_size=100,
        max_files=1,
        filetypes=["xes"],
        upload_id="event_log",
    )


def IMr_params_show_LLM():
    return html.Div([
        html.Div([
            html.Div(
                className="parameter-container",
                children=[
                    html.Div(
                        className="section-header",
                        children=html.H4("IMr Parameters", className="section-title"),
                    ),
                    html.Hr(),
                    html.H4("IMr sup:", className="parameter-name"),
                    html.Div([
                        dcc.Input(
                            id='sup_IMr_val',
                            type="number",
                            min=0,
                            max=1,
                            value=0.2,
                            step= 0.1
                        ),
                    ]),
                    html.Hr(),
                    html.Button(id="run_IMr_selector_LLM", children="Run IMr", className="btn-primary", n_clicks=0),
                    # html.Hr(),
                ]
            )
        ],
            className="flex-column align-center")
    ])

def show_petri_net_LLM(gviz):
    return html.Div(
                        className="visualization-wrapper",
                        children=[
                            # Graph and slider container
                            html.Div(
                                className="graph-slider-container",
                                style={"display": "flex", "alignItems": "center"},
                                children=[
                                    # Zoom slider on the left
                                    html.Div(
                                        className="slider-container",
                                        style={"width": "10%", "marginRight": "10px"},
                                        children=[
                                            dcc.Slider(
                                                id="zoom-slider",
                                                min=1.0,  # Minimum zoom level
                                                max=3.0,  # Maximum zoom level
                                                step=0.1,  # Increment steps
                                                value=1.0,  # Default zoom level
                                                marks={i: f"{i:.1f}" for i in [1.0, 1.5, 2.0,2.5,3.0]},
                                                vertical=True,  # Make it vertical
                                            ),
                                        ],
                                    ),
                                    # Graph visualization on the right
                                    html.Div(
                                        className="graph-container",
                                        style={
                                            "flexGrow": 1,
                                            "border": "1px solid #ddd",
                                            "height": "500px",
                                            "position": "relative",
                                            "overflow": "hidden",
                                        },
                                        children=[
                                            dash_interactive_graphviz.DashInteractiveGraphviz(
                                                id="gv",
                                                style={"transform": "scale(1)", "transformOrigin": "0 0"},
                                                dot_source=str(gviz),
                                                 engine = "dot"
                                            ),
                                        ],
                                    ),
                                ],
                            ),])

def show_petri_net_LLM2(gviz):
    return html.Div(
                        className="visualization-wrapper",
                        children=[
                            # Graph and slider container
                            html.Div(
                                className="graph-slider-container",
                                style={"display": "flex", "alignItems": "center"},
                                children=[
                                    # Zoom slider on the left
                                    html.Div(
                                        className="slider-container",
                                        style={"width": "10%", "marginRight": "10px"},
                                        children=[
                                            dcc.Slider(
                                                id="zoom-slider2",
                                                min=1.0,  # Minimum zoom level
                                                max=3.0,  # Maximum zoom level
                                                step=0.1,  # Increment steps
                                                value=1.0,  # Default zoom level
                                                marks={i: f"{i:.1f}" for i in [1.0, 1.5, 2.0,2.5,3.0]},
                                                vertical=True,  # Make it vertical
                                            ),
                                        ],
                                    ),
                                    # Graph visualization on the right
                                    html.Div(
                                        className="graph-container",
                                        style={
                                            "flexGrow": 1,
                                            "border": "1px solid #ddd",
                                            "height": "500px",
                                            "position": "relative",
                                            "overflow": "hidden",
                                        },
                                        children=[
                                            dash_interactive_graphviz.DashInteractiveGraphviz(
                                                id="gv2",
                                                style={"transform": "scale(1)", "transformOrigin": "0 0"},
                                                dot_source=str(gviz),
                                                 engine = "dot"
                                            ),
                                        ],
                                    ),
                                ],
                            ),])