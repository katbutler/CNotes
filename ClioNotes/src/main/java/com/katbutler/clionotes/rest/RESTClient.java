package com.katbutler.clionotes.rest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Wraps the native Android HTTP Client to make a clean REST Web Service API
 *
 * <br/> <br/>
 * Example Request: <br/> <br/>
 *
 * RESTClient.url("http://api.example.com/api/users") <br/>
 *               .withHeader("Accept", "application/json") <br/>
 *               .withHeader("Content-Type", "application/json") <br/>
 *               .withQueryString("id", "64533") <br/>
 *               .get(); <br/>
 */
public class RESTClient {

    public static RESTRequestData url(String url) {
        return new RESTRequestData(url);
    }


    /**
     * Data needed to make the REST Web Service request
     */
    public static class RESTRequestData {

        private String url = "";
        private Map<String, List<String>> queryStringMap = new HashMap<String, List<String>>();
        private Map<String, List<String>> headerMap = new HashMap<String, List<String>>();
        private String body = "";

        public RESTRequestData(String url) {
            this.url = url;
        }

        /**
         * Get the URL for the Requested Data
         * @return
         */
        public String getUrl() {
            return url;
        }

        /**
         * Get the Query String data for this request
         * @return
         */
        public Map<String, List<String>> getQueryString() {
            return queryStringMap;
        }

        /**
         * Get the Header data for this request
         * @return
         */
        public Map<String, List<String>> getHeaders() {
            return headerMap;
        }

        /**
         * Set the HTTP header values for keys
         *
         * @param name Name of the header field
         * @param value Value for the Name of the header
         * @return The {@link RESTRequestData} instance to chain calls together
         */
        public RESTRequestData withHeader(String name, String value) {
            if (headerMap.containsKey(name)) {
                List<String> values = headerMap.get(name);
                values.add(value);
            } else {
                List<String> values = new ArrayList<String>();
                values.add(value);
                headerMap.put(name, values);
            }
            return this;
        }


        /**
         * Set the Query String values for keys
         *
         * @param name Name of the header field
         * @param value Value for the Name of the header
         * @return The {@link RESTRequestData} instance to chain calls together
         */
        public RESTRequestData withQueryString(String name, String value) {
            if (queryStringMap.containsKey(name)) {
                List<String> values = queryStringMap.get(name);
                values.add(value);
            } else {
                List<String> values = new ArrayList<String>();
                values.add(value);
                queryStringMap.put(name, values);
            }
            return this;
        }

        /**
         * Return the URL Encoded parameters for the Query String Data.
         *
         * Will return null on {@link UnsupportedEncodingException}
         *
         * @return
         */
        public UrlEncodedFormEntity getQueryParametersUrlEncoded() {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Map.Entry<String, List<String>> entry : queryStringMap.entrySet()) {
                List<String> values = entry.getValue();
                for (String value : values ) {
                    params.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            UrlEncodedFormEntity encodedParams = null;

            try {
                encodedParams = new UrlEncodedFormEntity(params);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return encodedParams;
        }

        /**
         * Get the Query Paramters as a BasicHttpParams
         * @return
         */
        public BasicHttpParams getBasicHttpQueryParamters() {
            BasicHttpParams params = new BasicHttpParams();
            for (Map.Entry<String, List<String>> entry : queryStringMap.entrySet()) {
                List<String> values = entry.getValue();
                for (String value : values ) {
                    params.setParameter(entry.getKey(), value);
                }
            }
            return params;
        }

        /**
         * Get the Headers as an array of Http Headers
         * @return
         */
        public Header[] getHttpHeaders() {
            Header[] headers = new Header[headerMap.size()];
            int i = 0;
            for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
                List<String> values = entry.getValue();
                for (String value : values ) {
                    headers[i++] = new BasicHeader(entry.getKey(), value);
                }
            }
            return headers;
        }

        public void updateHeaders(final HttpRequestBase request) {
            for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
                List<String> values = entry.getValue();
                for (String value : values ) {
                    request.setHeader(entry.getKey(), value);
                }
            }
        }

        public String getBody() {
            return body;
        }

        public RESTRequestData withBody(String body) {
            this.body = body;
            return this;
        }

        /**
         * Execute this request as a GET
         * @return The {@link RESTResponse} from the web
         */
        public RESTResponse get() {
            return execute(RESTMethod.GET);
        }

        /**
         * Execute this request as a POST
         * @return The {@link RESTResponse} from the web
         */
        public RESTResponse post() {
            return execute(RESTMethod.POST);
        }

        /**
         * Execute this request as a PUT
         * @return The {@link RESTResponse} from the web
         */
        public RESTResponse put() {
            return execute(RESTMethod.PUT);
        }

        /**
         * Execute this request as a DELETE
         * @return The {@link RESTResponse} from the web
         */
        public RESTResponse delete() {
            return execute(RESTMethod.DELETE);
        }


        /**
         * Execute a Request with a specified REST Method
         * @param method
         * @return
         */
        public RESTResponse execute(RESTMethod method) {
            HttpClient httpclient = new DefaultHttpClient();

            try {
                switch (method) {
                    case GET:
                        // TODO implement a network GET request with RESTRequestData
                        HttpGet httpGet = new HttpGet(getUrl());

                        if (getQueryString().size() > 0)
                            httpGet.setParams(getBasicHttpQueryParamters());
                        if (getHeaders().size() > 0)
                            updateHeaders(httpGet);

                        // Execute HTTP POST Request
                        return new RESTResponse(httpclient.execute(httpGet));
                    case POST:
                        // TODO implement a network POST request with RESTRequestData
                        HttpPost httpPost = new HttpPost(getUrl());

                        if (getQueryString().size() > 0)
                            httpPost.setParams(getBasicHttpQueryParamters());
                        if (getHeaders().size() > 0)
                            updateHeaders(httpPost);
                        if (getBody().length() > 0) {
                            StringEntity entity = new StringEntity(getBody(), "UTF-8");
                            entity.setContentType("application/json");
                            entity.setContentEncoding("UTF-8");
                            httpPost.setEntity(entity);
                        }

                        // Execute HTTP POST Request
                        return new RESTResponse(httpclient.execute(httpPost));
                    case PUT:
                        // TODO implement a network PUT request with RESTRequestData
                        HttpPut httpPut = new HttpPut(getUrl());

                        if (getBody().length() > 0)
                            httpPut.setEntity(new StringEntity(getBody()));
                        if (getQueryString().size() > 0)
                            httpPut.setParams(getBasicHttpQueryParamters());
                        if (getHeaders().size() > 0)
                            updateHeaders(httpPut);

                        // Execute HTTP PUT Request
                        return new RESTResponse(httpclient.execute(httpPut));
                    case DELETE:
                        // TODO implement a network DELETE request with RESTRequestData
                        HttpDelete httpDelete = new HttpDelete(getUrl());
                        if (getHeaders().size() > 0)
                            updateHeaders(httpDelete);

                        // Execute HTTP POST Request
                        return new RESTResponse(httpclient.execute(httpDelete));
                    default:
                        throw new RuntimeException("Must specify a HTTP Method type");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    /**
     * Wrapper Class for the REST Response from the server
     */
    public static class RESTResponse {

        private HttpResponse response = null;
        private String body = null;

        public RESTResponse() {

        }

        /**
         * Construct a RESTResponse from an HttpResponse
         * @param rsp
         */
        public RESTResponse(HttpResponse rsp) {
            this.response = rsp;
        }

        /**
         * Get the Content Type of the Response
         * @return
         */
        public String getContentType() {
            return response.getFirstHeader("Content-Type").getValue();
        }

        public int getStatusCode() {
            return response.getStatusLine().getStatusCode();
        }

        /**
         * Get the Body of the Response
         * @return
         */
        public String getBody() {
            if (body != null)
                return body;

            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;

            // Convert the Response Input stream to a String
            try {
                InputStream is = response.getEntity().getContent();
                String line;


                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            body = sb.toString();
            return body;
        }

    }

    /**
     * Enumeration of the different types of REST Methods that can be performed
     */
    public static enum RESTMethod {
        GET,
        POST,
        PUT,
        DELETE
    }
}
