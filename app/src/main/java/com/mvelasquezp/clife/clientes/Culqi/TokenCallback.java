package com.mvelasquezp.clife.clientes.Culqi;

import org.json.JSONObject;

public interface TokenCallback {

    public void onSuccess(JSONObject token);

    public void onError(Exception error);

}
