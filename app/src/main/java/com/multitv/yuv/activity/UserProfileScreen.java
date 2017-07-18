package com.multitv.yuv.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.multitv.yuv.R;
import com.multitv.yuv.models.TagClass;
import com.multitv.yuv.utilities.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserProfileScreen extends AppCompatActivity {

    TagView tagGroup;
    private List<TagClass> tagList;
    private EditText nameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_screen);
        tagGroup = (TagView) findViewById(R.id.tag_group);
        nameTxt=(EditText) findViewById(R.id.nameTxt) ;
        nameTxt.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        prepareTags();
//        setTags("Afghanistan");
//        setTags("Algeria");


    }

    private void prepareTags() {
        tagList = new ArrayList<>();
        JSONArray jsonArray;
        JSONObject temp;
        try {
            jsonArray = new JSONArray(Constant.COUNTRIES);
            for (int i = 0; i < jsonArray.length(); i++) {
                temp = jsonArray.getJSONObject(i);
                tagList.add(new TagClass(temp.getString("code"), temp.getString("name")));

                setTags(temp.getString("name"));
            }

//            tagGroup.addTags(tagList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTags(CharSequence cs) {

        String text = cs.toString();
        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag;
        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).getName().toLowerCase().startsWith(text.toLowerCase())) {
                tag = new Tag(tagList.get(i).getName());
                tag.radius = 10f;
                tag.layoutColor = Color.parseColor(tagList.get(i).getColor());
                if (i % 2 == 0) // you can set deletable or not
                    tag.isDeletable = true;
                tags.add(tag);
            }
        }
        tagGroup.addTags(tags);

    }
}
