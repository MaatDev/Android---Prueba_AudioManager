package phone.end.call;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import audio.phone.com.silence.R;

public class Prueba_AudioManagerActivity extends Activity {
    /** Called when the activity is first created. */
	
	public static final String TAG = "base_de_datos_nosql";
	
	private TextView tv_number;
	private EditText et_number;
	
	//Base de datos NOSQL para almacenar más rápido
	
	private SharedPreferences sPref;
	private Editor sEditor;
	
	public static final String numberKey = "numero";
	
	//Filtro de la acción que recibe el BroadcastReceiver
	
	private IntentFilter filter;
	private final String intent_action = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	
	//El BroadcastReceiver
	
	private BR_PhoneCall phoneListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.tv_number = (TextView) findViewById(R.id.tv_number);
        this.et_number = (EditText) findViewById(R.id.et_number);
        
        this.sPref = this.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        this.sEditor = this.sPref.edit();
        
        this.filter = new IntentFilter();
        this.filter.addAction(intent_action);
        
        this.phoneListener = new BR_PhoneCall();
        
    }
    
    
    //Métodos de onClick
    
    public void guardar(View v){
    	
    	sEditor.putString(numberKey, et_number.getText().toString());
    	sEditor.commit();
    	    	
    	this.registerReceiver(this.phoneListener, filter);
    	
    }
    
    public void limpiar(View v){
    	
    	sEditor.remove(numberKey);
    	sEditor.commit();
    	
    	try{
    		
    		//Si no existe el receiver, lanza una excepción
    		
    		this.unregisterReceiver(this.phoneListener);
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
    
    public void mostrar(View v){
    	
    	tv_number.setText(sPref.getString(numberKey, " - "));
    	
    }
    
    
    
}