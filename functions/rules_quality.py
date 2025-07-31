
# Function to count groups where target_b occurs after target_a
def count_b_after_a(df, group_col, activity_col, target_a, target_b):
    def condition(activities):
        seen_a = False
        for act in activities:
            if act == target_a:
                seen_a = True
            elif act == target_b and seen_a:
                return True
        return False
    result = df.groupby(group_col)[activity_col].apply(condition)
    return result.sum()


def measure(log, rule):
    if rule['template'] in {'AtLeast1', 'AtMost1'}:
        activator = [rule['parameters'][0][0]]
        target = {rule['parameters'][0][0]}
    elif rule['template'] in {'Response', 'RespondedExistence', 'NotSuccession'}:
        activator = [rule['parameters'][0][0]]
        target = [rule['parameters'][1][0]]
    elif rule['template'] in {'Precedence'}:
        activator = [rule['parameters'][1][0]]
    elif rule['template'] in {'CoExistence', 'NotCoExistence'}:
        activator = [rule['parameters'][0][0],rule['parameters'][1][0]]
    else:
        return "error"

    case_count = log['case:concept:name'].nunique()
    focuse_df = log[log['concept:name'].isin(activator)]
    activated_count = focuse_df['case:concept:name'].nunique()

    if rule['template']== 'AtLeast1':
        satisfied_count = activated_count
    elif rule['template'] == 'AtMost1':
        activator_act = activator.pop()
        activator_counts = focuse_df.groupby('case:concept:name')['concept:name'].apply(lambda x: (x == activator_act).sum())
        satisfied_count = (activator_counts <= 1).sum()
    elif rule['template'] == 'Response':
        satisfied_count = count_b_after_a(
            focuse_df,
            group_col='case:concept:name',
            activity_col='concept:name',
            target_a=activator[0],
            target_b=target[0]
        )
    elif rule['template'] == 'Precedence':
        satisfied_count = focuse_df.nunique()
    elif rule['template'] == 'RespondedExistence':
        satisfied_count = focuse_df.nunique()
    elif rule['template'] == 'CoExistence':
        satisfied_count = focuse_df.nunique()
    elif rule['template'] == 'NotCoExistence':
        satisfied_count = focuse_df.nunique()
    elif rule['template'] == 'NotSuccession':
        satisfied_count = focuse_df.nunique()


    support = satisfied_count/case_count
    confidence = satisfied_count / activated_count

    return support,confidence




