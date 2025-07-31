package minerful.concept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import minerful.automaton.AutomatonFactory;
import minerful.automaton.SubAutomaton;
import minerful.automaton.utils.AutomatonUtils;
import minerful.concept.constraint.Constraint;
import minerful.concept.constraint.ConstraintsBag;
import minerful.concept.constraint.MetaConstraintUtils;
import minerful.concept.constraint.xmlenc.ConstraintsBagAdapter;
import minerful.index.LinearConstraintsIndexFactory;

import org.apache.log4j.Logger;

import dk.brics.automaton.Automaton;

@XmlRootElement(name = "processModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessModel extends Observable implements Observer {
    @XmlTransient
    private static Logger logger = Logger.getLogger(ProcessModel.class.getCanonicalName());
    @XmlTransient
    public static String DEFAULT_NAME = "Discovered model";

    @XmlElement(name = "declarative-model", required = true)
    @XmlJavaTypeAdapter(type = TreeSet.class, value = ConstraintsBagAdapter.class)
    public ConstraintsBag bag;
    @XmlAttribute
    private String name;
    @XmlElement
    private TaskCharArchive taskCharArchive;
    @XmlTransient
    public static final String MINERFUL_XMLNS = "https://github.com/cdc08x/MINERful/";

    protected ProcessModel() {
    }

    public ProcessModel(ConstraintsBag bag) {
        this(new TaskCharArchive(bag.getTaskChars()), bag, DEFAULT_NAME);
    }

    public ProcessModel(ConstraintsBag bag, String name) {
        this(new TaskCharArchive(bag.getTaskChars()), bag, name);
    }

    public ProcessModel(TaskCharArchive taskCharArchive, ConstraintsBag bag) {
        this(taskCharArchive, bag, DEFAULT_NAME);
    }

    public ProcessModel(TaskCharArchive taskCharArchive, ConstraintsBag bag, String name) {
        this.taskCharArchive = taskCharArchive;
        this.bag = bag;
        this.name = name;
        this.bag.addObserver(this);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TaskChar> getProcessAlphabet() {
        return this.bag.getTaskChars();
    }

    public Automaton buildAutomaton() {
        return buildAutomatonByBondHeuristic();
    }

    public Automaton buildAlphabetAcceptingAutomaton() {
        return AutomatonFactory.fromRegularExpressions(new ArrayList<String>(0), this.taskCharArchive.getIdentifiersAlphabet());
    }

    public Collection<SubAutomaton> buildSubAutomata() {
        return buildSubAutomata(AutomatonFactory.NO_LIMITS_IN_ACTIONS_FOR_SUBAUTOMATA);
    }

    public Collection<SubAutomaton> buildSubAutomata(int maxActions) {
        NavigableMap<Character, Collection<String>> regExpsMap = new TreeMap<Character, Collection<String>>();
        Collection<String> regExps = null;
        Collection<Constraint> cns = null;
        String alphabetLimitingRegularExpression = AutomatonUtils.createRegExpLimitingTheAlphabet(this.taskCharArchive.getIdentifiersAlphabet());

        for (TaskChar tChr : this.bag.getTaskChars()) {

            cns = this.bag.getConstraintsOf(tChr);
            regExps = new ArrayList<String>(cns.size());

            for (Constraint con : cns) {
                if (!con.isMarkedForExclusion()) {
                    regExps.add(con.getRegularExpression());
                }
            }
            regExps.add(alphabetLimitingRegularExpression);

            regExpsMap.put(tChr.identifier, regExps);
        }

        if (maxActions > AutomatonFactory.NO_LIMITS_IN_ACTIONS_FOR_SUBAUTOMATA)
            return AutomatonFactory.subAutomataFromRegularExpressionsInMultiThreading(regExpsMap, this.taskCharArchive.getIdentifiersAlphabet(), maxActions);
        else
            return AutomatonFactory.subAutomataFromRegularExpressionsInMultiThreading(regExpsMap, this.taskCharArchive.getIdentifiersAlphabet());
    }

    /*
     * This turned out to be the best heuristic for computing the automaton!
     */
    protected Automaton buildAutomatonByBondHeuristic() {
        Collection<String> regularExpressions = null;
        Collection<Constraint> constraints = LinearConstraintsIndexFactory.getAllUnmarkedConstraintsSortedByBoundsSupportFamilyConfidenceInterestFactorHierarchyLevel(this.bag);
        regularExpressions = new ArrayList<String>(constraints.size());
        for (Constraint con : constraints) {
            regularExpressions.add(con.getRegularExpression());
        }
        return AutomatonFactory.fromRegularExpressions(regularExpressions, this.taskCharArchive.getIdentifiersAlphabet());
    }

    public TaskCharArchive getTaskCharArchive() {
        return this.taskCharArchive;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProcessModel [bag=");
        builder.append(bag);
        builder.append(", name=");
        builder.append(name);
        builder.append(", taskCharArchive=");
        builder.append(taskCharArchive);
        builder.append("]");
        return builder.toString();
    }

    public static ProcessModel generateNonEvaluatedBinaryModel(TaskCharArchive taskCharArchive) {
        ProcessModel proMod = null;

        Iterator<TaskChar>
                actIter = taskCharArchive.getTaskChars().iterator(),
                auxActIter = null,
                extraActIter = null;
        TaskChar
                auxActiParam1 = null,
                auxActiParam2 = null,
                extraActiParam3 = null;
        Collection<Constraint>
                conSet = new TreeSet<Constraint>(),
                auxConSet = null,
                extraConSet = null;
        Collection<TaskChar> activitiesLeftToCombine = new TreeSet<TaskChar>(taskCharArchive.getTaskChars());

        while (actIter.hasNext()) {
            auxActiParam1 = actIter.next();

            auxConSet = MetaConstraintUtils.getAllDiscoverableExistenceConstraints(auxActiParam1);
            auxConSet = MetaConstraintUtils.createHierarchicalLinks(auxConSet);

            conSet.addAll(auxConSet);

            activitiesLeftToCombine.remove(auxActiParam1);
            auxActIter = activitiesLeftToCombine.iterator();

            auxConSet = new TreeSet<Constraint>();
            while (auxActIter.hasNext()) {
                auxActiParam2 = auxActIter.next();

                auxConSet = MetaConstraintUtils.getAllDiscoverableRelationConstraints(auxActiParam1, auxActiParam2);
                auxConSet.addAll(MetaConstraintUtils.getAllDiscoverableRelationConstraints(auxActiParam2, auxActiParam1));

                auxConSet = MetaConstraintUtils.createHierarchicalLinks(auxConSet);
                conSet.addAll(auxConSet);

                // Alessio: injection for non-DECLARE constraint with 3 variables
				/*
				extraActIter = activitiesLeftToCombine.iterator();
				while(extraActIter.hasNext()){
					extraActiParam3 = extraActIter.next();
					extraConSet = MetaConstraintUtils.getAllDiscoverableExtraConstraints(auxActiParam1, auxActiParam2, extraActiParam3);
					extraConSet.addAll(MetaConstraintUtils.getAllDiscoverableExtraConstraints(auxActiParam1, extraActiParam3, auxActiParam2));

//					extraConSet = MetaConstraintUtils.createHierarchicalLinks(extraConSet);
					conSet.addAll(extraConSet);

				}
				*/
            }
        }
        ConstraintsBag bag = new ConstraintsBag(taskCharArchive.getTaskChars(), conSet);
        proMod = new ProcessModel(taskCharArchive, bag);

        return proMod;
    }

    public SortedSet<Constraint> getAllConstraints() {
        return LinearConstraintsIndexFactory.getAllConstraints(bag);
    }

    public SortedSet<Constraint> getAllUnmarkedConstraints() {
        return LinearConstraintsIndexFactory.getAllUnmarkedConstraints(bag);
    }

    public int howManyConstraints() {
        return bag.howManyConstraints();
    }

    public int howManyUnmarkedConstraints() {
        return bag.howManyUnmarkedConstraints();
    }

    public int howManyTasks() {
        return this.taskCharArchive.size();
    }

    public Set<TaskChar> getTasks() {
        return this.taskCharArchive.getTaskChars();
    }

    public void resetMarks() {
        for (TaskChar tCh : this.bag.getTaskChars()) {
            for (Constraint con : this.bag.getConstraintsOf(tCh)) {
                con.resetMarks();
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.setChanged();
        this.notifyObservers(arg);
        this.clearChanged();
    }

    /**
     * Unite the input process models into a new one containing the union of the alphabets and constraints of them.
     *
     * @param model1
     * @param model2
     * @return
     */
    public static ProcessModel union(ProcessModel model1, ProcessModel model2) {
        Set<TaskChar> taskChars = model1.getTasks();
        Set<TaskChar> taskChars2 = model2.getTasks();
        taskChars.addAll(taskChars2);

        Collection<Constraint> constraints = model1.getAllConstraints();
        Collection<Constraint> constraints2 = model2.getAllConstraints();
        constraints.addAll(constraints2);

        ConstraintsBag newBag = new ConstraintsBag(taskChars, constraints);
        return new ProcessModel(newBag, "Union");
    }

    /**
     * Return a new process model containing all the constraints not in common between the input models.
     * The alphabets are united anyway.
     *
     * @param model1
     * @param model2
     * @return
     */
    public static ProcessModel difference(ProcessModel model1, ProcessModel model2) {
        Set<TaskChar> taskChars = model1.getTasks();
        taskChars.addAll(model2.getTasks());

        Collection<Constraint> constraints1 = model1.getAllConstraints();
        Collection<Constraint> constraints2 = model2.getAllConstraints();
        constraints1.removeAll(model2.getAllConstraints());
        constraints2.removeAll(model1.getAllConstraints());

        constraints1.addAll(constraints2);

        ConstraintsBag newBag = new ConstraintsBag(taskChars, constraints1);
        return new ProcessModel(newBag, "Union");
    }

}