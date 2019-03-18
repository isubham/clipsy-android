package com.subhamkumar.clipsy.panel;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jkcarino.rtexteditorview.RTextEditorButton;
import com.jkcarino.rtexteditorview.RTextEditorToolbar;
import com.jkcarino.rtexteditorview.RTextEditorView;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.panel.fragments.InsertLinkDialogFragment;
import com.subhamkumar.clipsy.panel.fragments.InsertTableDialogFragment;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;

public class editor extends AppCompatActivity {

    public void saveClip() {

        wrapper saveClip = new wrapper() {

            @Override
            public Map<String, String> _getHeaders() {
                Map params = new HashMap<String, String>();
                params.put(Constants.header_authentication, getToken());
                return params;
            }

            // save clip
            @Override
            public Map makeParams() {
                Map params = new HashMap<String, String>();
                params.put(Constants.CLIP_CONTENT, editor.getHtml());
                return params;
            }

            @Override
            public int setHttpMethod() {
                return Request.Method.POST;
            }

            @Override
            public String setHttpUrl() {
                return Constants.request_clip_create;
            }

            @Override
            public void makeVolleyRequest(StringRequest stringRequest) {
                Volley.newRequestQueue(editor.this).add(stringRequest);
            }

            @Override
            public void handleResponse(String response) {
                Gson gson = new Gson();
                ClipApiResonse clipApiResonse = gson.fromJson(response, ClipApiResonse.class);

                if(clipApiResonse.success.equals("1")) {
                    currentclip = clipApiResonse.data.get(0);
                    Toast.makeText(editor.this, clipApiResonse.message, Toast.LENGTH_SHORT).show();
                }
                else{
                    // TODO handle no changes or empty or error
                }


            }
        };
        saveClip.makeRequest();
    }

    Clip currentclip;

    private void updateClip(final String clipId) {
        wrapper update_clip = new wrapper() {

            @Override
            public Map<String, String> _getHeaders() {
                Map params = new HashMap<String, String>();
                params.put(Constants.header_authentication, getToken());
                return params;
            }

            @Override
            public Map makeParams() {
                Map params = new HashMap<String, String>();
                params.put(Constants.CLIP_CONTENT, editor.getHtml());
                return params;
            }

            @Override
            public void handleResponse(String response) {

                Gson gson = new Gson();
                ClipApiResonse clipApiResonse = gson.fromJson(response, ClipApiResonse.class);

                if(clipApiResonse.success.equals("1")) {
                    currentclip = clipApiResonse.data.get(0);
                    Toast.makeText(editor.this, clipApiResonse.message, Toast.LENGTH_SHORT).show();
                }
                else{
                    // TODO handle no changes or empty or error
                }

            }
             @Override
            public int setHttpMethod() {
                return Request.Method.POST;
            }

            @Override
            public String setHttpUrl() {
                return String.format(Constants.request_clip_update, clipId);
            }

            @Override
            public void makeVolleyRequest(StringRequest stringRequest) {
                Volley.newRequestQueue(editor.this).add(stringRequest);
            }
        };

        update_clip.makeRequest();
    }

    private String getToken() {
        return getIntent().getExtras().getString("token");
    }


    private void applyChangesToClip() {
        if(currentclip.clip_id.equals(Constants.response_invalid_clip_id)) {
            saveClip();
        }
        else{
            updateClip(currentclip.clip_id);
        }
    }

    private static final String TAG = "RTextEditorView";

    private static final int DIALOG_TEXT_FORE_COLOR_ID = 0;
    private static final int DIALOG_TEXT_BACK_COLOR_ID = 1;

    private RTextEditorView editor;

    private void initialHtml(String html) {
        editor.setHtml(html);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        init();



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

    Bundle bundle;
    private void init() {

        setInvalidClipOrValidClip();

        editor = findViewById(R.id.editor_view);
        // Enable keyboard's incognito mode
        editor.setIncognitoModeEnabled(true);

        RTextEditorToolbar editorToolbar = findViewById(R.id.editor_toolbar);
        editorToolbar.setEditorView(editor);
        // Set initial clip_content
        initialHtml("<p>Once upon a time ...</p>");

        // TODO ? save every edit.
        editor.setOnTextChangeListener(new RTextEditorView.OnTextChangeListener() {
            @Override
            public void onTextChanged(String content) {
                Log.d(TAG, "onTextChanged: " + content);
                //  ? applyChangesToClip();
            }
        });
    }

    private void setInvalidClipOrValidClip() {
        currentclip = new Clip();
        currentclip.clip_id = Constants.response_invalid_clip_id;

        bundle = getIntent().getExtras();

        if(bundle.containsKey("clip")) {
            currentclip = new Clip(bundle);
        }
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
                applyChangesToClip();
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
