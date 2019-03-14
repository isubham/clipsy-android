package com.subhamkumar.clipsy.panel;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jkcarino.rtexteditorview.RTextEditorButton;
import com.jkcarino.rtexteditorview.RTextEditorToolbar;
import com.jkcarino.rtexteditorview.RTextEditorView;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.panel.fragments.InsertLinkDialogFragment;
import com.subhamkumar.clipsy.panel.fragments.InsertTableDialogFragment;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class editor extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        return null;
    }

    String user_id, clip_type = Constants.PUBLIC;

    @Override
    public Map makeParams() {
        Map params = new HashMap<String, String>();
        params.put(Constants.VISIBILITY, clip_type);
        params.put(Constants.USER, user_id);
        params.put(Constants.CLIP_CONTENT, editor.getHtml());
        return params;
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }

    @Override
    public String setHttpUrl() {
        return getString(R.string.request_clip_create);
    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(editor.this).add(stringRequest);
    }

    @Override
    public void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has(Constants.STATUS)) {
                editor.this.finish();
            } else {
                Toast.makeText(this, jsonObject.getString(Constants.STATUS), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e(Constants.JSON_EX, e.getMessage());
        }
    }

    /*
    public void selectType(View V) {
        clip_type = V.getId() == R.id._private ? Constants.PRIVATE : Constants.PUBLIC;
    }
    */

    String clip_id;

    public void create_clip(String action) {

        if(action.equals("save")){
            makeRequest();
        }
        else{
            wrapper update_clip = new wrapper() {
                @Override
                public Map<String, String> _getHeaders() {
                    return null;
                }

                @Override
                public Map makeParams() {
                Map params = new HashMap<String, String>();
                params.put(Constants.VISIBILITY, clip_type);
                params.put(Constants.CLIP_CONTENT, editor.getHtml());
                params.put(Constants.FX, Constants.OPERATION_UPDATE_CLIP);
                params.put("id", clip_id);
                    return params;
                }

                @Override
                public void handleResponse(String response) {

                }
                 @Override
                public int setHttpMethod() {
                    return Request.Method.POST;
                }

                @Override
                public String setHttpUrl() {
                    return getString(R.string.request_clip_update);
                }


                @Override
                public void makeVolleyRequest(StringRequest stringRequest) {

                    Volley.newRequestQueue(editor.this).add(stringRequest);

                }
            };

            update_clip.makeRequest();

        }
    }

    private static final String TAG = "RTextEditorView";

    private static final int DIALOG_TEXT_FORE_COLOR_ID = 0;
    private static final int DIALOG_TEXT_BACK_COLOR_ID = 1;

    private RTextEditorView editor;

    private void initialHtml(String html) {
        editor.setHtml(html);
    }

    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        if (getIntent().getExtras() != null) {
            user_id = getIntent().getExtras().getString("token");
            action = "save";
            if (getIntent().getExtras().containsKey("clip_id")){
                clip_id = getIntent().getExtras().getString("clip_id");

                wrapper fetch_clip = new wrapper() {
                    @Override
                    public Map<String, String> _getHeaders() {
                        return null;
                    }

                    @Override
                    public Map makeParams() {
                        return null;
                    }

                    @Override
                    public void handleResponse(String response) {

                    }

                    @Override
                    public int setHttpMethod() {
                        return Request.Method.GET;
                    }

                    @Override
                    public String setHttpUrl() {
                        return getString(R.string.request_clip_read);
                    }


                    @Override
                    public void makeVolleyRequest(StringRequest stringRequest) {

                    }
                };
                fetch_clip.makeRequest();

                action = "update";
            }
        }


        editor = findViewById(R.id.editor_view);
        // Enable keyboard's incognito mode
        editor.setIncognitoModeEnabled(true);

        RTextEditorToolbar editorToolbar = findViewById(R.id.editor_toolbar);
        editorToolbar.setEditorView(editor);

        // Set initial clip_content
        initialHtml("<p>Once upon a time ...</p>");


        // Listen to the editor's text changes
        editor.setOnTextChangeListener(new RTextEditorView.OnTextChangeListener() {
            @Override
            public void onTextChanged(String content) {
                Log.d(TAG, "onTextChanged: " + content);
            }
        });

        // Insert Link
        RTextEditorButton insertLinkButton = findViewById(R.id.insert_link);
        insertLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertLinkDialogFragment dialog = InsertLinkDialogFragment.newInstance();
                dialog.setOnInsertClickListener(onInsertLinkClickListener);
                dialog.show(getSupportFragmentManager(), "insert-link-dialog");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_undo:
                editor.undo();
                break;
            case R.id.action_redo:
                editor.redo();
                break;
            case R.id.create_menu_write:
                create_clip(action);
                break;
            case R.id.create_menu_close:
                editor.this.finish();
                break;
        }
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.setOnTextChangeListener(null);
        editor.removeAllViews();
        editor.destroy();
        editor = null;
    }

    private final InsertTableDialogFragment.OnInsertClickListener onInsertTableClickListener =
            new InsertTableDialogFragment.OnInsertClickListener() {
                @Override
                public void onInsertClick(int colCount, int rowCount) {
                    editor.insertTable(colCount, rowCount);
                }
            };

    private final InsertLinkDialogFragment.OnInsertClickListener onInsertLinkClickListener =
            new InsertLinkDialogFragment.OnInsertClickListener() {
                @Override
                public void onInsertClick(@NonNull String title, @NonNull String url) {
                    editor.insertLink(title, url);
                }
            };


}
