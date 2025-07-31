package minerful;

import minerful.concept.TaskCharArchive;
//import minerful.params.InputLogCmdParameters;
import minerful.params.SystemCmdParameters;
import minerful.postprocessing.params.PostProcessingCmdParameters;
import minerful.reactive.io.JanusVariantOutputManagementLauncher;
import minerful.reactive.params.JanusVariantCmdParameters;
import minerful.reactive.params.JanusPrintParameters;
import minerful.utils.MessagePrinter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Map;

/**
 * Class to start from terminal Janus variant analysis
 */
public class JanusVariantAnalysisStarter extends MinerFulMinerStarter {
    public static MessagePrinter logger = MessagePrinter.getInstance(JanusVariantAnalysisStarter.class);

    @Override
    public Options setupOptions() {
        Options cmdLineOptions = new Options();

        Options systemOptions = SystemCmdParameters.parseableOptions(),
//                outputOptions = OutputModelParameters.parseableOptions(),
                postPrOptions = PostProcessingCmdParameters.parseableOptions(),
//                viewOptions = ViewCmdParameters.parseableOptions(),
                janusViewOptions = JanusPrintParameters.parseableOptions(),
//                chkOptions = CheckingCmdParameters.parseableOptions(),
//                inputLogOptions = InputLogCmdParameters.parseableOptions(),
//                inpuModlOptions = InputModelParameters.parseableOptions(),
                janusOptions = JanusVariantCmdParameters.parseableOptions();

        for (Object opt : systemOptions.getOptions()) {
            cmdLineOptions.addOption((Option) opt);
        }
//        for (Object opt : outputOptions.getOptions()) {
//            cmdLineOptions.addOption((Option) opt);
//        }
        for (Object opt : postPrOptions.getOptions()) {
            cmdLineOptions.addOption((Option) opt);
        }
//        for (Object opt : viewOptions.getOptions()) {
//            cmdLineOptions.addOption((Option) opt);
//        }
//        for (Object opt : chkOptions.getOptions()) {
//            cmdLineOptions.addOption((Option) opt);
//        }
//        for (Object opt : inputLogOptions.getOptions()) {
//            cmdLineOptions.addOption((Option) opt);
//        }
//        for (Object opt : inpuModlOptions.getOptions()) {
//            cmdLineOptions.addOption((Option) opt);
//        }
        for (Object opt : janusViewOptions.getOptions()) {
            cmdLineOptions.addOption((Option) opt);
        }
        for (Object opt : janusOptions.getOptions()) {
            cmdLineOptions.addOption((Option) opt);
        }

        return cmdLineOptions;
    }

    public static void main(String[] args) {
        JanusVariantAnalysisStarter checkStarter = new JanusVariantAnalysisStarter();
        Options cmdLineOptions = checkStarter.setupOptions();

        SystemCmdParameters systemParams =
                new SystemCmdParameters(
                        cmdLineOptions,
                        args);
//        OutputModelParameters outParams =
//                new OutputModelParameters(
//                        cmdLineOptions,
//                        args);
        PostProcessingCmdParameters preProcParams =
                new PostProcessingCmdParameters(
                        cmdLineOptions,
                        args);
//        CheckingCmdParameters chkParams =
//                new CheckingCmdParameters(
//                        cmdLineOptions,
//                        args);
//        InputLogCmdParameters inputLogParams =
//                new InputLogCmdParameters(
//                        cmdLineOptions,
//                        args);
//        InputModelParameters inpuModlParams =
//                new InputModelParameters(
//                        cmdLineOptions,
//                        args);
//        ViewCmdParameters viewParams =
//                new ViewCmdParameters(
//                        cmdLineOptions,
//                        args);
        JanusPrintParameters janusViewParams =
                new JanusPrintParameters(
                        cmdLineOptions,
                        args);
        JanusVariantCmdParameters janusParams =
                new JanusVariantCmdParameters(
                        cmdLineOptions,
                        args);

        MessagePrinter.configureLogging(systemParams.debugLevel);

        if (systemParams.help) {
            systemParams.printHelp(cmdLineOptions);
            System.exit(0);
        }
        double execTimeStart = System.currentTimeMillis();

        JanusVariantAnalysisLauncher variantAnalysis = new JanusVariantAnalysisLauncher(janusParams, systemParams, preProcParams, janusViewParams);
        Map<String, Float> result = variantAnalysis.checkVariants();

        TaskCharArchive alphabet = variantAnalysis.getAlphabetDecoder();
        new JanusVariantOutputManagementLauncher().manageVariantOutput(result, janusParams, janusViewParams, systemParams, alphabet, variantAnalysis.getMeasurementsSpecification1(), variantAnalysis.getMeasurementsSpecification2());

        double execTimeEnd = System.currentTimeMillis();
        logger.info("Total execution time: " + (execTimeEnd - execTimeStart));
    }

}
