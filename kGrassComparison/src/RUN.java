import alg.KGCommon;
import alg.KGWeighted;
import alg.KGUnweighted;
import alg.KGWeighted_L2;


public class RUN {

    public static void main(String[] args) {
        final String dataPath = args[0];

        final double targetRatio = Double.parseDouble(args[1]);

        final String reconstructionErrorType = args[2];

        final String modelType = args[3];
        System.out.println("Model Type: " + modelType);

        KGCommon module;

        if(modelType.equals("we")){
            if(reconstructionErrorType.equals("1")) module = new KGWeighted(reconstructionErrorType);
            else if(reconstructionErrorType.equals("2")) module = new KGWeighted_L2(reconstructionErrorType);
            else{
                System.out.println("Reconstruction Type Error");
                return;
            }

        }else if(modelType.equals("uwe")){
            module = new KGUnweighted(reconstructionErrorType);
        }
        else{
            System.out.println("Model Type Error");
            return;
        }

        module.readGraph(dataPath);
        module.summarize(dataPath, targetRatio);

    }


}
