package phone.end.call;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

//http://prasanta-paul.blogspot.com/2010/09/call-control-in-android.html
//http://stackoverflow.com/questions/1853220/retrieve-incoming-calls-phone-number-in-android
public class BR_PhoneCall extends BroadcastReceiver{

	private final String TAG = getClass().getSimpleName();
	public static final String ACTION_INTENT=TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	public static final String variable_BR_type = "phone";
		
	private final String variable_phone_number = "incoming_number";
	private String number="";
	
	private AudioManager aManager;
	
	private Context context;
	
	private com.android.internal.telephony.ITelephony telephonyService;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.v(TAG, "Estoy en "+TAG+": onReceive");
		
		this.context = context;
		   
		//Sacar en bundle que trae del intent recibido
		 
		 Bundle bundle = intent.getExtras();		 
			 
		 //Obtener el número de la llamada
		 
		 this.number = bundle.getString(variable_phone_number);
		 
		 //Abrir la base de datos NOSQL 
		 
		 SharedPreferences sPref = context.getSharedPreferences(Prueba_AudioManagerActivity.TAG
				 , Context.MODE_PRIVATE);
		 
		 //Comparar el número de la llamada con el número guardado
		 
		 if(PhoneNumberUtils.compare(sPref.getString(Prueba_AudioManagerActivity.numberKey,
				 ""), this.number)){
			 
			 getTeleService();
			 
			 telephonyService.endCall();
			 
			 //Mostrar el mensaje del número silenciado
			 
			 Toast.makeText(context, "Llamada silenciada: "+this.number, Toast.LENGTH_LONG).show();
			 
			 //Clase manejadora para silenciar la llamada
			 
			 this.aManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			 this.aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			 
			 //Listener para saber cuando cuelga el teléfono
			 
			 MyPhoneStateListener phoneListener=new MyPhoneStateListener();
	 		 
			 TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			 
			 telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
			 
		 }
		 		 
		 		 		 
	}	
	
	public class MyPhoneStateListener extends PhoneStateListener {

		  public void onCallStateChanged(int state,String incomingNumber){

		  switch(state){

		    case TelephonyManager.CALL_STATE_IDLE:

		      Log.v(TAG, "IDLE");
		      
		      //Devolver el estado anterior del silencio
		      
		      aManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

		    break;

		    case TelephonyManager.CALL_STATE_OFFHOOK:

		      Log.v("TAG", "OFFHOOK");

		    break;

		    case TelephonyManager.CALL_STATE_RINGING:

		      Log.d("TAG", "RINGING");

		    break;

		    }

		  } 

		}
	
	private void getTeleService() {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            // Java reflection to gain access to TelephonyManager's
            // ITelephony getter
            Log.v(TAG, "Get getTeleService...");
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            this.telephonyService = (ITelephony) m.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);
        }
}


}
