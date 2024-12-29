package ao.co.isptec.aplm.binasjc;

import android.app.Activity;
import android.widget.TextView;


//Actividade que serve como auxílio para o alertDialog do Serviço criado WifiService , pois ele precisa do contexto da activida actual
//Auxílio também para poder obter o TextView de modo a inserir nele as mensagens recebidas via wifidirect de outros dispositivos
public class ActivityContextProvider {

    private static Activity currentActivity;
    private static TextView outputMsg;

    public static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static TextView getOutputMsg() {
        return outputMsg;
    }

    public static void setOutputMsg(TextView outputMsg) {
        ActivityContextProvider.outputMsg = outputMsg;
    }
}
