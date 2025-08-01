from dash import html, dcc
import dash_bootstrap_components as dbc


layout = dbc.Container(
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
        # Introduction Section
        # html.Div(
        #     className="intro-section",
        #     children=[
        #         html.H2("Welcome to IMr!", className="section-title"),
        #         html.P(
        #             "IMr enhances process discovery by integrating domain knowledge and process rules, allowing for the creation of high-quality process models that align with both event data and expert knowledge.",
        #             className="content",
        #         ),
        #         dcc.Link(
        #             "Get Started",
        #             href="/main",
        #             className="cta-link"
        #         ),
        #     ],
        # ),
        # Background and Context Section
        html.Div(
            className="context-section",
            children=[html.Div([
    html.H1("Welcome to the LLM-Assisted Process Discovery Tool"),

    html.P(
        "Traditional process discovery techniques rely solely on event logs extracted from "
        "information systems to derive process models. While these models are useful for tasks "
        "such as conformance checking and process improvement, they often overlook valuable domain "
        "knowledge expressed in natural language. As a result, the discovered models may not fully "
        "reflect the true process as understood by experts."
    ),

    html.P(
        "This tool is designed to bridge that gap by enabling domain experts to incorporate their "
        "knowledge into the discovery process alongside the event log. The underlying discovery technique "
        "builds on the principles of Inductive Mining and is extended to support declarative rules "
        "extracted from textual inputs. These rules guide the discovery algorithm in constructing "
        "process models that are better aligned with both observed behavior and domain expertise."
    ),

    html.P(
        "Domain experts can provide textual input either before the discovery process begins or after an "
        "initial model has been generated and reviewed. In both cases, users can initiate a chat with a "
        "selected Large Language Model (LLM), which collaborates with backend services to extract declarative "
        "constraints from the provided descriptions. When ambiguities arise, the LLM is encouraged to engage "
        "in clarification dialogues, ensuring the extracted rules are accurate and meaningful."
    ),

    html.P(
        "Proceed to the next page to upload your own event log and start interacting with the tool to discover "
        "process models guided by both data and expert insight."
    )
])
            ],
        ),
dcc.Link(
                    "Get Started",
                    href="/LLM",
                    className="cta-link"
                ),
    ],
)