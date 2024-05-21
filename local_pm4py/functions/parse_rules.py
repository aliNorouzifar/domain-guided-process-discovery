def parse_constraints(file_path):
    constraints_dict = {
        'atmost1': [],
        'existence': [],
        'response': [],
        'precedence': [],
        'coexistence': [],
        'noncoexistence': [],
        'nonsuccession': [],
        'responded_existence': []
    }

    with open(file_path, 'r') as file:
        lines = file.readlines()

        for line in lines:
            line = line.strip()
            if line.startswith('at-most'):
                activity = line[len('at-most('):-1].strip()
                constraints_dict['atmost1'].append((activity,))
            elif line.startswith('existence'):
                activity = line[len('existence('):-1].strip()
                constraints_dict['existence'].append((activity,))
            elif line.startswith('response'):
                activities = line[len('response('):-1].split(',')
                activities = tuple(activity.strip() for activity in activities)
                constraints_dict['response'].append(activities)
            elif line.startswith('precedence'):
                activities = line[len('precedence('):-1].split(',')
                activities = tuple(activity.strip() for activity in activities)
                constraints_dict['precedence'].append(activities)
            elif line.startswith('co-existence'):
                activities = line[len('co-existence('):-1].split(',')
                activities = tuple(activity.strip() for activity in activities)
                constraints_dict['coexistence'].append(activities)
            elif line.startswith('not-co-existence'):
                activities = line[len('not-co-existence('):-1].split(',')
                activities = tuple(activity.strip() for activity in activities)
                constraints_dict['noncoexistence'].append(activities)
            elif line.startswith('not-succession'):
                activities = line[len('not-succession('):-1].split(',')
                activities = tuple(activity.strip() for activity in activities)
                constraints_dict['nonsuccession'].append(activities)
            elif line.startswith('responded-existence'):
                activities = line[len('responded-existence('):-1].split(',')
                activities = tuple(activity.strip() for activity in activities)
                constraints_dict['responded_existence'].append(activities)

    return constraints_dict


