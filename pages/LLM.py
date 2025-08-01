from dash import html, dcc, dash_table
import dash_bootstrap_components as dbc
import dash_uploader as du
import json
import dash_interactive_graphviz
from prolysis.util.redis_connection import redis_client
import dash_ag_grid as dag
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


AI_MODEL_DEFAULTS = {
    AIProviders.GOOGLE.value: "gemini-2.5-flash",
    AIProviders.OPENAI.value: "gpt-4.1",
    AIProviders.DEEPSEEK.value: "deepseek-reasoner",
}


def create_layout():
    redis_client.set('rules', json.dumps([]))
    redis_client.set('dimensions', json.dumps([]))
    redis_client.set('activities', json.dumps([]))

    return dbc.Container(
        className="page-container",
        children=[
            # Navigation Links
            html.Div(
                className="nav-links",
                children=[
                    dcc.Link("Introduction", href="/", className="nav-link"),
                    dcc.Link("LLM", href="/LLM", className="nav-link"),
                    dcc.Link("About Me", href="/about_me", className="nav-link"),
                ],
            ),
            # Tool Name and Description
            html.Div(
                className="tool-name-container",
                children=[
                    html.H1("LLM-Guided Process Discovery with Domain Knowledge", className="tool-name"),
                ],
            ),
            # Main Content Area
            html.Div(
                className="vertical-container",
                children=[
                    # Left Panel: Parameter Settings
                    create_top_panel(),
                    # Right Panel: Visualization Blocks
                    create_bottom_panel(),
                    html.Div(id="dummy"),
                ],
            ),
        ],
    )



def create_top_panel():
    return html.Div(
        id="top-panel",
        className="top-panel",
        children=[
            html.Div(
                style={"display": "flex", "gap": "20px"},
                children=[
                    # ---------- Right Column (merged) ----------
                    html.Div(
                        style={
                            "flex": "1",
                            "border": "1px solid #ccc",
                            "padding": "20px",
                            "borderRadius": "8px",
                            "backgroundColor": "#f9f9f9",
                        },
                        children=[
                            # Upload + IMr Sup in one row
                            html.Div(
                                style={"display": "flex", "gap": "20px", "alignItems": "center",
                                       "marginBottom": "10px"},
                                children=[
                                    html.Div(
                                        style={"flex": 2},
                                        children=[
                                            get_upload_component("upload-data")
                                        ]
                                    ),
                                    html.Div(
                                        style={"flex": 1, "display": "flex", "alignItems": "center", "gap": "10px"},
                                        children=[
                                            html.Label("IMr sup:", style={"marginBottom": "0", "whiteSpace": "nowrap"}),
                                            dcc.Input(
                                                id='sup_IMr_val',
                                                type="number",
                                                min=0,
                                                max=1,
                                                value=0.2,
                                                step=0.1,
                                                style={"height": "30px", "flex": "1"}
                                            )
                                        ]
                                    )
                                ]
                            ),

                            # Rule Table
                            dag.AgGrid(
                                id="rule-table",
                                columnDefs=columnDefs,
                                rowData=[],
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
                                style={"height": "240px"}
                            ),

                            # Button
                            html.Button("Run Discovery", id="run_IMr_selector_LLM", n_clicks=0, style={"marginTop": "10px"})
                        ]
                    ),
                    # Icon Between Columns
                    html.Div(
                        style={
                            "display": "flex",
                            "alignItems": "center",
                            "justifyContent": "center",
                            "padding": "0px",
                        },
                        children=[
                            html.Img(
                                src="/assets/arrow-icon.png",
                                # Place your icon in the assets folder (e.g., arrow-icon.png)
                                style={
                                    "width": "50px",
                                    "height": "50px",
                                    "objectFit": "contain",
                                    "opacity": 1
                                }
                            )
                        ]
                    ),
                    # ---------- Left Column (spans both rows) ----------
                    html.Div(
                        style={
                            "flex": "1",
                            "border": "1px solid #ccc",
                            "padding": "20px",
                            "borderRadius": "8px",
                            "backgroundColor": "#f9f9f9",
                        },
                        children=[html.Div(
    children=[
        # -------- First Row --------
        html.Div(
            style={"display": "flex", "gap": "20px", "marginBottom": "10px"},
            children=[
                # Select LLM Provider
                html.Div(
                    style={"flex": 1, "display": "flex", "alignItems": "center", "gap": "10px"},
                    children=[
                        html.Label("Select LLM Provider:", style={"marginBottom": "0", "whiteSpace": "nowrap"}),
                        dcc.Dropdown(
                            id="llm-provider",
                            options=[
                                {"label": provider.value, "value": provider.value}
                                for provider in AIProviders
                            ],
                            placeholder="Choose an LLM provider...",
                            style={"flex": "1", "height": "35px"},
                        )
                    ]
                ),
                # Model Name
                html.Div(
                    style={"flex": 1, "display": "flex", "alignItems": "center", "gap": "10px"},
                    children=[
                        html.Label("Model Name:", style={"marginBottom": "0", "whiteSpace": "nowrap"}),
                        dcc.Input(
                            id="model-name",
                            type="text",
                            placeholder="Enter model name...",
                            style={"flex": "1", "height": "30px"},
                        )
                    ]
                ),
            ]
        ),

        # -------- Second Row --------
        html.Div(
            style={"display": "flex", "gap": "20px", "alignItems": "center"},
            children=[
                # API Key
                html.Div(
                    style={"flex": 1, "display": "flex", "alignItems": "center", "gap": "10px"},
                    children=[
                        html.Label("API Key:", style={"marginBottom": "0", "whiteSpace": "nowrap"}),
                        dcc.Input(
                            id="api-key",
                            type="password",
                            placeholder="Enter API key...",
                            style={"flex": "1", "height": "30px"},
                        )
                    ]
                ),
                # Initialize Button
                html.Div(
                    style={"flex": 1, "textAlign": "center"},
                    children=[
                        html.Button(
                            "Initialize LLM",
                            id="init-llm-btn",
                            n_clicks=0,
                            style={
                                "height": "35px",
                                "fontSize": "16px",
                                "padding": "5px 20px"
                            }
                        )
                    ]
                )
            ]
        ),

        # Output message
        html.Div(id="llm-init-output", style={"marginTop": "10px", "color": "green"})
    ]
),
                            html.Hr(),
                            html.Div(
                                className="chat_box",
                                id="chat-box-wrapper",
                                # style={"flex": "1"},
                                children=[
                                    html.Div(
                                        id="chat-placeholder",
                                        style={
                                            "height": "260px",
                                            "border": "1px solid #ccc",
                                            "backgroundColor": "#f0f0f0",
                                            "display": "flex",
                                            "justifyContent": "center",
                                            "alignItems": "center",
                                            "textAlign": "center",
                                            "color": "#888",
                                            "marginBottom": "20px"
                                        },
                                        children="Upload an event log and initialize an LLM to start interacting with the assistant."
                                    ),
                                    html.Div(
                                        id="chat-conversation",
                                        )
                                ]
                            ),
                        ]
                    ),
                ]
            )
        ]
    )




def create_bottom_panel():
    return html.Div(
        id="right-panel",
        className="bottom-panel",
        children=[
            html.Div(
                id="petri_net_LLM",
                style={
                    "height": "300px",
                    "border": "1px solid #ccc",
                    "backgroundColor": "#f0f0f0",
                    "position": "relative",
                },
                children=[
                    html.Div(
                        id="petri-net-placeholder",
                        style={
                            "position": "absolute",
                            "top": 0,
                            "bottom": 0,
                            "left": 0,
                            "right": 0,
                            "display": "flex",
                            "justifyContent": "center",
                            "alignItems": "center",
                            "color": "#888",
                            "zIndex": 1,  # Ensure it's on top if needed
                        },
                        children="The process model will appear here."
                    ),
                    html.Div(id="petri-net-graph-container")  # This is where you load your Graphviz graph
                ]
            )
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
            text="Upload your event log here!",
        default_style={"height": "40px"}
        )


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
                                        style={"width": "3%", "marginRight": "5px"},
                                        children=[
                                            dcc.Slider(
                                                id="zoom-slider",
                                                min=1.0,  # Minimum zoom level
                                                max=3.0,  # Maximum zoom level
                                                step=0.1,  # Increment steps
                                                value=1.0,  # Default zoom level
                                                marks={i: f"{i:.1f}" for i in [1.0, 1.5, 2.0,2.5,3.0]},
                                                vertical=True,  # Make it vertical
                                                verticalHeight = 300
                                            ),
                                        ],
                                    ),
                                    # Graph visualization on the right
                                    html.Div(
                                        className="graph-container",
                                        style={
                                            "flexGrow": 1,
                                            "border": "1px solid #ddd",
                                            "height": "300px",
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
                            ),html.Button("Download the Model", id="download-button", n_clicks=0,
                                                style={"marginTop": "10px"}),
                            dcc.Download(id="download")
                        ])