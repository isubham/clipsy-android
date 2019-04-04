package com.subhamkumar.clipsy.panel;

import android.app.Dialog;
import android.net.nsd.NsdManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jkcarino.rtexteditorview.RTextEditorButton;
import com.jkcarino.rtexteditorview.RTextEditorToolbar;
import com.jkcarino.rtexteditorview.RTextEditorView;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.auth.change_password;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.panel.fragments.InsertLinkDialogFragment;
import com.subhamkumar.clipsy.panel.fragments.InsertTableDialogFragment;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.Tools;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class editor extends AppCompatActivity {

    private void saveClip() {

        wrapper saveClip = new wrapper() {

            @Override
            public Map<String, String> _getHeaders() {
                Map params = new HashMap<String, String>();
                params.put(Constants.header_authentication, getToken());
                return params;
            }

            @Override
            protected void handleErrorResponse(VolleyError error) {
                showNetworkUnavailableDialog();
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

                if (clipApiResonse.success.equals("1")) {
                    currentclip = clipApiResonse.data.get(0);
                    Toast.makeText(editor.this, clipApiResonse.message, Toast.LENGTH_SHORT).show();
                    closeEditor();
                } else {
                    // TODO handle no changes or empty or error
                }

                Tools.hideNetworkLoadingDialog(networkLoadingDialog, "editor hide");

            }
        };
        saveClip.makeRequest();
    }

    private Clip currentclip;

    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(editor.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            applyChangesToClip();
        });

        dialog.show();

    }

    private void updateClip(final String clipId) {
        wrapper update_clip = new wrapper() {
            @Override
            protected void handleErrorResponse(VolleyError error) {

                showNetworkUnavailableDialog();
            }

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

                if (clipApiResonse.success.equals("1")) {
                    currentclip = clipApiResonse.data.get(0);
                    Toast.makeText(editor.this, clipApiResonse.message, Toast.LENGTH_SHORT).show();
                    closeEditor();
                } else {
                    // TODO handle no changes or empty or error
                }

                Tools.hideNetworkLoadingDialog(networkLoadingDialog, "editor hide");
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


    private void closeEditor() {
        this.finish();
    }

    private String getToken() {
        return Objects.requireNonNull(getIntent().getExtras()).getString("token");
    }

    private String getClipId() {
        return Objects.requireNonNull(getIntent().getExtras()).getString("clip_id");
    }


    private void applyChangesToClip() {
        if (isDirty()) {

            Tools.showNetworkLoadingDialog(networkLoadingDialog, "editor show");

            if (currentclip.clip_id.equals(Constants.response_invalid_clip_id)) {
                saveClip();
            } else {
                updateClip(currentclip.clip_id);
            }

        } else {
            Toast.makeText(this, "Nothing to save.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDirty() {
        return !isEditorEmpty() || isEditorUpdated();
    }

    @Override
    public void onBackPressed() {

        unSavedDialogCheck();
    }

    private void unSavedDialogCheck() {
        if (isDirty()) {

            final Dialog dialog = new Dialog(editor.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_unsaved_clip_confirmation);

            dialog.findViewById(R.id.dialog_unsaved_clip_menu_save).setOnClickListener(v -> {
                dialog.dismiss();
                saveClip();
            });
            dialog.findViewById(R.id.dialog_unsaved_clip_menu_close).setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });

            dialog.show();

        } else {
            finish();
        }
    }

    private static final String TAG = "RTextEditorView";

    private static final int DIALOG_TEXT_FORE_COLOR_ID = 0;
    private static final int DIALOG_TEXT_BACK_COLOR_ID = 1;

    private RTextEditorView editor;

    private void initialHtml(String html) {
    }

    private Dialog networkLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        networkLoadingDialog = new Dialog(editor.this, R.style.CustomDialogTheme);

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

    private Bundle bundle;

    private boolean isEditorEmpty() {
        return editor.getHtml().equals("<p><br></p>");
    }

    private boolean isEditorUpdated() {
        return editor.getHtml().equals(fetchedClip);
    }

    private void init() {

        setInvalidClipOrValidClip();

        editor = findViewById(R.id.editor_view);
        // Enable keyboard's incognito mode
        editor.setIncognitoModeEnabled(true);

        RTextEditorToolbar editorToolbar = findViewById(R.id.editor_toolbar);
        editorToolbar.setEditorView(editor);
        // Set initial clip_content
        // initialHtml("<p>Once upon a time ...</p>");

        // TODO ? save every edit.
        editor.setOnTextChangeListener(content -> {
            Log.d(TAG, "onTextChanged: " + content);
            //  ? applyChangesToClip();
        });

    }

    String fetchedClip;

    private void setInvalidClipOrValidClip() {
        currentclip = new Clip();
        currentclip.clip_id = Constants.response_invalid_clip_id;

        bundle = getIntent().getExtras();

        if (Objects.requireNonNull(bundle).containsKey("clip")) {

            wrapper getClip = new wrapper() {
                @Override
                public Map makeParams() {
                    return new HashMap<String, String>();
                }

                @Override
                protected void handleErrorResponse(VolleyError error) {
                    showNetworkUnavailableDialog();
                }

                @Override
                public void handleResponse(String response) {
                    ClipApiResonse apiResonse = new Gson().fromJson(response, ClipApiResonse.class);
                    currentclip = apiResonse.data.get(0);
                    editor.setHtml(currentclip.clip_content);
                    fetchedClip = currentclip.clip_content;
                }

                @Override
                public void makeVolleyRequest(StringRequest stringRequest) {
                    Volley.newRequestQueue(editor.this).add(stringRequest);
                }

                @Override
                public int setHttpMethod() {
                    return Request.Method.GET;
                }

                @Override
                public String setHttpUrl() {
                    String url = String.format(Constants.request_clip_read, getClipId());
                    return url;
                }

                @Override
                public Map<String, String> _getHeaders() {
                    Map params = new HashMap<String, String>();
                    params.put(Constants.header_authentication, getToken());
                    return params;
                }
            };

            getClip.makeRequest();
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
            case R.id.create_menu_close: {
                if (!isDirty()) {
                    editor.this.finish();
                } else {
                    unSavedDialogCheck();
                }
            }
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
