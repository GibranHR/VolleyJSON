package com.itche.gibranhr.volleyjson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTextViewResult;
    private RequestQueue mQueue;

    Button btnConsumirWs;
    EditText edtUrlWs;
    String respuestaServidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = findViewById(R.id.mTextViewResult);
        mQueue = Volley.newRequestQueue(this);
        edtUrlWs = (EditText) findViewById(R.id.edtUrl);

        btnConsumirWs = (Button) findViewById(R.id.btnConsumir);
        Button buttonParse = findViewById(R.id.button_parse);

        btnConsumirWs.setOnClickListener(this);
        buttonParse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnConsumir) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = edtUrlWs.getText().toString();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mTextViewResult.setText("La respuesta es: " + response + "\n");
                            respuestaServidor = response;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mTextViewResult.setText("¡No funciona el WebService!");
                        }
                    });
            queue.add(stringRequest);
        } else {
            jsonParse(respuestaServidor);
        }
    }

    private void jsonParse(String url) {
        url = edtUrlWs.getText().toString();

        if (respuestaServidor != null) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("employees");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject employee = jsonArray.getJSONObject(i);

                                    String firstName = employee.getString("firstname");
                                    int age = employee.getInt("age");
                                    String mail = employee.getString("mail");

                                    mTextViewResult.append("\n" + "Respuesta parseada: " +
                                            firstName + ", " + String.valueOf(age) + ", " + mail + "\n\n");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            mQueue.add(request);
        } else {
            Log.e("ParsearJSON", "No se obtuvo respuesta JSON del servidor");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Error al solicitar JSON del servidor. Revise el LogCat por posibles errores!",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
