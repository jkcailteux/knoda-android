package networking;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import unsorted.Logger;

/**
 * Created by nick on 1/22/14.
 */
public class MultipartRequest extends Request<String> {

    private final Response.Listener<String> listener;
    private final HttpEntity entity;
    private byte[] body;

    public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, HttpEntity entity) {
        super(Method.PUT, url, errorListener);

        this.listener = listener;
        this.entity = entity;
        writeBody();
        this.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

    }

    private void writeBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        body = bos.toByteArray();
    }

    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", entity.getContentType().getValue());
        Logger.log(headers.toString());
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return body;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        StringWriter writer = new StringWriter();
        InputStream is = new ByteArrayInputStream(response.data);
        try {
            IOUtils.copy(is, writer);
        } catch (IOException e) {
            Logger.log(e.toString());
        }
        return Response.success(writer.toString(), getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }
}