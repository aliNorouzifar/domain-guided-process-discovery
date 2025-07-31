from pm4py.algo.filtering.log.variants import variants_filter
import pandas as pd
import pm4py
from functions.utils import read_json_file, save_json_file



def encode(log,data):
    variants = variants_filter.get_variants(log)
    trace_variants = {"\u003c" + ",".join(variant) + "\u003e": len(instances) for variant, instances in
                        variants.items()}

    for tr in data:
        for constraint in data[tr]:
            if constraint != "MODEL":
                if 2 in data[tr][constraint]:
                    data[tr][constraint] = "violated"
                elif 3 in data[tr][constraint]:
                    data[tr][constraint] = "satisfied"
                else:
                    # data[tr][constraint] = "satisfied"
                    # data[tr][constraint] = "violated" #we consider vac_satisfied as violated
                    data[tr][constraint] = "v_satisfied"

    df = pd.DataFrame(data).T
    df_count =pd.Series(trace_variants)
    repeated_indices = df.index.repeat(df_count)
    result = df.loc[repeated_indices].reset_index()
    rules_meas = {}
    for cl in result.columns:
        if cl != 'index' and cl != 'MODEL':
            rules_meas[cl] = {}
            rules_meas[cl]['support'] = len(result[result[cl] != 'violated']) / len(result)
            rules_meas[cl]['confidence'] = len(result[result[cl] == 'satisfied']) / len(result[result[cl] != 'v_satisfied'])

    return result, rules_meas


def generate_natural_language(c,a,b):
    if c == "RespondedExistence":
        description = f"If {a} occurs, {b} occurs as well."
    elif c == "CoExistence":
        description = f"{a} and {b} always occur together."
    elif c == "Response":
        description = f" If {a} occurs, then {b} occurs after it."
    elif c == "AlternateResponse":
        description = f"If {a} occurs, then {b} occurs afterwards before {a} recurs."
    elif c == "ChainResponse":
        description = f"If {a} occurs, then {b} occurs immediately after it."
    elif c == "Precedence":
        description = f"{b} occurs only if preceded by {a}."
    elif c == "AlternatePrecedence":
        description = f"{b} occurs only if preceded by {a} with no other {b} in between."
    elif c == "ChainPrecedence":
        description = f"{b} occurs only if {a} occurs immediately before it. "
    elif c == "Succession":
        description = f"{a} occurs if and only if it is followed by {b}."
    elif c == "AlternateSuccession":
        description = f"{a} and {b} occur if and only if they follow one another, alternating."
    elif c == "ChainSuccession":
        description = f"{a} and {b} occurs if and only if {b} immediately follows {a}."
    elif c == "Init":
        description = f"{a} is the first to occur."
    elif c == "End":
        description = f"{a} is the last to occur."
    elif c == "Absence":
        description = f"{a} must never occur."
    elif c == "AtMost1":
        description = f"{a} occurs at most once."
    elif c == "AtMost2":
        description = f"{a} occurs at most two times."
    elif c == "AtMost3":
        description = f"{a} occurs at most three times."
    elif c == "AtLeast1":
        description = f"{a} occurs at least once."
    elif c == "AtLeast2":
        description = f"{a} occurs at least two times."
    elif c == "AtLeast3":
        description = f"{a} occurs at least three times."
    else:
        description = f"The constraint is un known!"

    return description

# CoExistence, RespondedExistence, Response, Precedence, AtMost1, AtLeast1, NotSuccession
def parse_constraints(line):
    # constraints_dict = {
    #     'AtMost1': [],
    #     'AtLeast1': [],
    #     'Response': [],
    #     'Precedence': [],
    #     'CoExistence': [],
    #     'NotSuccession': [],
    #     'NotCoExistence': [],
    #     'RespondedExistence': []
    # }

    rule = {}

    line = line.strip()
    if line.startswith('AtMost1'):
        activity = line[len('AtMost1('):-1].strip()
        rule['template'] = "AtMost1"
        rule['parameters'] = [[activity]]
        # constraints_dict['AtMost1'].append((activity,))
    elif line.startswith('AtLeast1'):
        activity = line[len('AtLeast1('):-1].strip()
        rule['template'] = "AtLeast1"
        rule['parameters'] = [[activity]]
        # constraints_dict['AtLeast1'].append((activity,))
    elif line.startswith('Response'):
        activities = line[len('Response('):-1].split(',')
        activities = tuple(activity.strip() for activity in activities)
        rule['template'] = "Response"
        rule['parameters'] = [[activities[0]],[activities[1]]]
        # constraints_dict['Response'].append(activities)
    elif line.startswith('Precedence'):
        activities = line[len('Precedence('):-1].split(',')
        activities = tuple(activity.strip() for activity in activities)
        rule['template'] = "Precedence"
        rule['parameters'] = [[activities[0]], [activities[1]]]
        # constraints_dict['Precedence'].append(activities)
    elif line.startswith('CoExistence'):
        activities = line[len('CoExistence('):-1].split(',')
        activities = tuple(activity.strip() for activity in activities)
        rule['template'] = "CoExistence"
        rule['parameters'] = [[activities[0]], [activities[1]]]
    elif line.startswith('NotCoExistence'):
        activities = line[len('NotCoExistence('):-1].split(',')
        activities = tuple(activity.strip() for activity in activities)
        rule['template'] = "NotCoExistence"
        rule['parameters'] = [[activities[0]], [activities[1]]]
        # constraints_dict['NotCoExistence'].append(activities)
    elif line.startswith('NotSuccession'):
        activities = line[len('NotSuccession('):-1].split(',')
        activities = tuple(activity.strip() for activity in activities)
        rule['template'] = "NotSuccession"
        rule['parameters'] = [[activities[0]], [activities[1]]]
        # constraints_dict['NotSuccession'].append(activities)
    elif line.startswith('RespondedExistence'):
        activities = line[len('RespondedExistence('):-1].split(',')
        activities = tuple(activity.strip() for activity in activities)
        rule['template'] = "RespondedExistence"
        rule['parameters'] = [[activities[0]], [activities[1]]]
        # constraints_dict['RespondedExistence'].append(activities)
    return rule





