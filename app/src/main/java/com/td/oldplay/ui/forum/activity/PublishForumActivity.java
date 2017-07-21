package com.td.oldplay.ui.forum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.td.oldplay.R;
import com.td.oldplay.base.BaseFragmentActivity;
import com.td.oldplay.base.EventMessage;
import com.td.oldplay.base.GlideImageLoader;
import com.td.oldplay.base.adapter.recyclerview.CommonAdapter;
import com.td.oldplay.base.adapter.recyclerview.base.ViewHolder;
import com.td.oldplay.bean.ForumBean;
import com.td.oldplay.bean.ForumDetial;
import com.td.oldplay.http.HttpManager;
import com.td.oldplay.http.callback.OnResultCallBack;
import com.td.oldplay.http.subscriber.HttpSubscriber;
import com.td.oldplay.utils.GlideUtils;
import com.td.oldplay.utils.ToastUtil;
import com.td.oldplay.widget.CustomTitlebarLayout;
import com.tencent.mm.opensdk.utils.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zuichu.picker.AudioPicker;
import me.zuichu.picker.FilePicker;
import me.zuichu.picker.ImagePicker;
import me.zuichu.picker.VideoPicker;
import me.zuichu.picker.bean.AudioItem;
import me.zuichu.picker.bean.FileItem;
import me.zuichu.picker.bean.ImageItem;
import me.zuichu.picker.bean.VideoItem;
import me.zuichu.picker.ui.audio.AudioGridActivity;
import me.zuichu.picker.ui.image.ImageGridActivity;
import me.zuichu.picker.ui.video.VideoGridActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PublishForumActivity extends BaseFragmentActivity implements View.OnClickListener {

    @BindView(R.id.title)
    CustomTitlebarLayout title;
    @BindView(R.id.publish_title)
    EditText publishTitle;
    @BindView(R.id.publish_content)
    EditText publishContent;
    @BindView(R.id.publisd_video)
    TextView publisdVideo;
    @BindView(R.id.publisd_Audio)
    TextView publisdAudio;
    @BindView(R.id.publisd_pic)
    TextView publisdPic;
    @BindView(R.id.publish_send)
    TextView publishSend;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private int type; //0 发布 1 编辑
    private String id;

    private ImagePicker imagePicker;
    private VideoPicker videoPicker;
    private AudioPicker audioPicker;

    private List<Object> datas = new ArrayList<>();
    private MediaAdapter adapter;

    public static final int IMAGE = 100;
    public static final int AUDIO = 101;
    public static final int VIDEO = 102;

    private HashMap<String, Object> paramC = new HashMap<>();
    private HashMap<String, RequestBody> paramPi = new HashMap<>();
    private HashMap<String, RequestBody> paramVi = new HashMap<>();
    private HashMap<String, RequestBody> paramVo = new HashMap<>();
    private List<String> imageList = new ArrayList<>();
    private List<String> videList = new ArrayList<>();
    private List<String> audioList = new ArrayList<>();

    private String titleS;
    private String content;
    private String contentId;

    private File file;
    private RequestBody requestFile;
    private int successContent;
    private int count;
    private ForumDetial forumDetial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_forum);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 0);
        id = getIntent().getStringExtra("id");
        forumDetial = (ForumDetial) getIntent().getSerializableExtra("model");
        paramC.put("userId", userId);
        paramC.put("boardId", id);
        initView();
    }

    private void initView() {
        if (type == 0) {
            title.setTitle("发布");
        } else {
            title.setTitle("编辑");
            setForumData();
        }
        title.setOnLeftListener(this);
        publisdAudio.setOnClickListener(this);
        publisdPic.setOnClickListener(this);
        publisdVideo.setOnClickListener(this);
        publishSend.setOnClickListener(this);
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setSelectLimit(20);
        imagePicker.setShowCamera(true);
        imagePicker.setMultiMode(true);

        videoPicker = VideoPicker.getInstance();
        videoPicker.setImageLoader(new GlideImageLoader());
        videoPicker.setSelectLimit(5);
        videoPicker.setShowCamera(true);
        videoPicker.setMultiMode(true);

        audioPicker = audioPicker.getInstance();
        audioPicker.setImageLoader(new GlideImageLoader());
        audioPicker.setSelectLimit(5);
        audioPicker.setShowCamera(true);
        audioPicker.setMultiMode(true);

        recycler.setLayoutManager(new GridLayoutManager(mContext, 4));
        adapter = new MediaAdapter(mContext, R.layout.item_medial, datas);
        recycler.setAdapter(adapter);

    }

    private void setForumData() {
        if (forumDetial != null) {
            if (forumDetial.topic != null) {
                paramC.put("boardId", forumDetial.topic.boardId);
                paramC.put("topicId", forumDetial.topic.topicId);
                publishContent.setText(forumDetial.topic.content);
                publishTitle.setText(forumDetial.topic.title);
            }

            if (forumDetial.imageUrlList != null) {
                for (String s : forumDetial.imageUrlList) {
                    datas.add(new ImageItem(s, 1));
                }
            }

            if (forumDetial.speechUrlList != null) {
                for (String s : forumDetial.speechUrlList) {
                    datas.add(new AudioItem(s, 1));
                }
            }
            if (forumDetial.videoUrlList != null) {
                for (String s : forumDetial.videoUrlList) {
                    datas.add(new VideoItem(s, 1));
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.left_text:
                finish();
                break;
            case R.id.publisd_Audio:
                intent = new Intent(this, AudioGridActivity.class);
                startActivityForResult(intent, AUDIO);
                break;
            case R.id.publisd_video:
                intent = new Intent(this, VideoGridActivity.class);
                startActivityForResult(intent, VIDEO);
                break;
            case R.id.publisd_pic:
                intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE);
                break;
            case R.id.publish_send:
                if (checkInput()) {
                    publicOne();
                }
                break;
        }

    }

    private void publicOne() {
        ToastUtil.show("正在上传中");
        setMedia();
        showLoading();
        HttpManager.getInstance().postForumContent(paramC, new HttpSubscriber<String>(new OnResultCallBack<String>() {

            @Override
            public void onSuccess(String s) {
                contentId = s;
                Log.e("===", contentId + "---------------");
                if (forumDetial != null) {
                    contentId = forumDetial.topic.topicId;
                }
                publishOther();

            }

            @Override
            public void onError(int code, String errorMsg) {
                ToastUtil.show(errorMsg);
            }
        }));
    }

    private void setMedia() {
        if (datas != null && datas.size() > 0) {
            for (Object obj : datas) {
                if (obj instanceof ImageItem) {
                    if (((ImageItem) obj).type == 1) {
                        imageList.add((((ImageItem) obj).path));
                    } else {
                        file = new File(((ImageItem) obj).path);
                        requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        paramPi.put("picFile\"; filename=\"" + file.getName(), requestFile);
                    }

                } else if (obj instanceof VideoItem) {

                    if (((VideoItem) obj).type == 1) {
                        videList.add(((VideoItem) obj).path);
                    } else {
                        file = new File(((VideoItem) obj).path);
                        requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        paramVi.put("picFile\"; filename=\"" + file.getName(), requestFile);
                    }

                } else if (obj instanceof AudioItem) {

                    if (((AudioItem) obj).type == 1) {
                        audioList.add(((AudioItem) obj).path);
                    } else {
                        file = new File(((AudioItem) obj).path);
                        requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        paramVo.put("picFile\"; filename=\"" + file.getName(), requestFile);
                    }

                }
            }


        }
    }

    private void publishOther() {
        paramPi.put("topicId", toRequestBody(contentId));
        paramVi.put("topicId", toRequestBody(contentId));
        paramVo.put("topicId", toRequestBody(contentId));
        if (paramPi.size() >= 2) {
            count++;
            HttpManager.getInstance().postForumPic(paramPi, MediaSubscriber);
        }
        if (paramVi.size() >= 2) {
            count++;
            HttpManager.getInstance().postForumVideo(paramVi, MediaSubscriber);
        }
        if (paramVo.size() >= 2) {
            count++;
            HttpManager.getInstance().postForumVoicec(paramVo, MediaSubscriber);
        }


    }

    private RequestBody toRequestBody(String para) {
        return RequestBody.create(MediaType.parse("text.plain"), para);
    }

    private boolean checkInput() {
        titleS = publishTitle.getText().toString();
        if (TextUtils.isEmpty(titleS)) {
            ToastUtil.show("请输入标题");
            return false;
        }
        paramC.put("title", titleS);


        content = publishContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show("请输入内容");
            return false;
        }
        paramC.put("content", content);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_IMAGE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                datas.addAll(images);
                adapter.notifyDataSetChanged();
            }
        }

        if (resultCode == VideoPicker.RESULT_VIDEO_ITEMS) {
            if (data != null && requestCode == VIDEO) {
                ArrayList<VideoItem> videos = (ArrayList<VideoItem>) data.getSerializableExtra(VideoPicker.EXTRA_RESULT_VIDEO_ITEMS);
                datas.addAll(videos);
                adapter.notifyDataSetChanged();
            }
        }
        if (resultCode == AudioPicker.RESULT_AUDIO_ITEMS) {
            if (data != null && requestCode == AUDIO) {
                ArrayList<AudioItem> audios = (ArrayList<AudioItem>) data.getSerializableExtra(AudioPicker.EXTRA_RESULT_AUDIO_ITEMS);
                datas.addAll(audios);
                adapter.notifyDataSetChanged();
            }
        }

    }

    public class MediaAdapter extends CommonAdapter<Object> {
        public MediaAdapter(Context context, int layoutId, List<Object> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Object o, final int position) {
            if (o instanceof ImageItem) {
                if (((ImageItem) o).type == 1) {
                    GlideUtils.setImage(mContext, ((ImageItem) o).path, (ImageView) holder.getView(R.id.item_media_iv));
                } else {
                    GlideUtils.setPhotoImage(mContext, ((ImageItem) o).path, (ImageView) holder.getView(R.id.item_media_iv));
                }


            } else if (o instanceof VideoItem) {
                if (((VideoItem) o).type == 1) {
                    GlideUtils.setImage(mContext, ((VideoItem) o).path, (ImageView) holder.getView(R.id.item_media_iv));
                } else {
                    GlideUtils.setPhotoImage(mContext, ((VideoItem) o).path, (ImageView) holder.getView(R.id.item_media_iv));
                }

            } else if (o instanceof AudioItem) {
                holder.setImageResource(R.id.item_media_iv, R.mipmap.icon_audi);

            }
            holder.setOnClickListener(R.id.item_media_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PublishForumActivity.this.datas.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private HttpSubscriber<String> MediaSubscriber = new HttpSubscriber<>(new OnResultCallBack<String>() {

        @Override
        public void onSuccess(String s) {
            successContent++;
            Log.e("===", successContent + "        " + count);
            if (successContent == count) {
                hideLoading();
                successContent = 0;
                EventBus.getDefault().post(new EventMessage("publish"));
                ToastUtil.show("发布成功");
                finish();
            }

        }

        @Override
        public void onError(int code, String errorMsg) {

        }
    });
}
