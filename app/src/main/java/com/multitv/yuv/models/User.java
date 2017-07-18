package com.multitv.yuv.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by delhivery on 9/29/2016.
 */
public class User implements Parcelable {

   public String uid;

   public String otp;


   public String  keywords;

   public String  location;

   public String contact_no;

   public String  image;

   public String  provider;

   public String  following_count;

   public String id;

   public String  first_name;

   public String  follow_count;

   public String  about_me;

   private String token;

   public String  email;

   private String created;

   public String  dob;

   public String  age;

   public String  last_name;

   public String  gender;

   public String app_session_id;


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.uid);
      dest.writeString(this.otp);
      dest.writeString(this.keywords);
      dest.writeString(this.location);
      dest.writeString(this.contact_no);
      dest.writeString(this.image);
      dest.writeString(this.provider);
      dest.writeString(this.following_count);
      dest.writeString(this.id);
      dest.writeString(this.first_name);
      dest.writeString(this.follow_count);
      dest.writeString(this.about_me);
      dest.writeString(this.token);
      dest.writeString(this.email);
      dest.writeString(this.created);
      dest.writeString(this.dob);
      dest.writeString(this.age);
      dest.writeString(this.last_name);
      dest.writeString(this.gender);
      dest.writeString(this.app_session_id);
   }

   public User() {
   }

   protected User(Parcel in) {
      this.uid = in.readString();
      this.otp = in.readString();
      this.keywords = in.readString();
      this.location = in.readString();
      this.contact_no = in.readString();
      this.image = in.readString();
      this.provider = in.readString();
      this.following_count = in.readString();
      this.id = in.readString();
      this.first_name = in.readString();
      this.follow_count = in.readString();
      this.about_me = in.readString();
      this.token = in.readString();
      this.email = in.readString();
      this.created = in.readString();
      this.dob = in.readString();
      this.age = in.readString();
      this.last_name = in.readString();
      this.gender = in.readString();
      this.app_session_id = in.readString();
   }

   public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
      @Override
      public User createFromParcel(Parcel source) {
         return new User(source);
      }

      @Override
      public User[] newArray(int size) {
         return new User[size];
      }
   };
}
