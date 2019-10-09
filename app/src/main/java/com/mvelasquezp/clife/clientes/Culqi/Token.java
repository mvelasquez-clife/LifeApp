package com.mvelasquezp.clife.clientes.Culqi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mvelasquezp.clife.clientes.tools.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Token {

    Config config = new Config();

    private static final String URL = "/tokens/";

    private String api_key;

    private TokenCallback listener;

    public Token(String api_key){
        this.api_key = api_key;
        this.listener = null;
    }

    public void createToken(Context context, Card card, final TokenCallback listener) {
        //aqui, de alguna manera debo poder meter un parametro metadata tipo Hashmap

        this.listener = listener;

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody = new JSONObject();
            jsonBody.put("card_number", card.getCard_number());
            jsonBody.put("cvv", card.getCvv());
            jsonBody.put("expiration_month", card.getExpiration_month());
            jsonBody.put("expiration_year", card.getExpiration_year());
            jsonBody.put("email", card.getEmail());
        } catch (Exception ex){
Log.v(Constantes.APP_NAME, "ERROR: "+ex.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, config.url_base+URL,jsonBody, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    listener.onSuccess(response);
                } catch (Exception ex){
                    listener.onError(ex);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;

                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            json = new String(response.data);
                            json = trimMessage(json, "message");
Log.e(Constantes.APP_NAME, json);
                            break;
                        default:
Log.e(Constantes.APP_NAME, "codigo: " + response.statusCode);
                            break;
                    }
                    //Additional cases
                }
                listener.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + Token.this.api_key);
                return headers;
            }

        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);

    }

    public String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
Log.e(Constantes.APP_NAME, e.getLocalizedMessage());
            return null;
        }

        return trimmedString;
    }


}
