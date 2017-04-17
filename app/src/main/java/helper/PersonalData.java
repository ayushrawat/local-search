package helper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ayushrawat on 17/04/17.
 */

public class PersonalData implements Parcelable {
    String name;
    String line1;
    String area;
    String reference;
    String phone;
    String time;

    public PersonalData(String name, String line1, String area, String reference, String phone, String time) {
        this.name = name;
        this.line1 = line1;
        this.area = area;
        this.reference = reference;
        this.phone = phone;
        this.time = time;
    }

    public PersonalData(Parcel in) {
        this.name = in.readString();
        this.line1 = in.readString();
        this.area = in.readString();
        this.reference = in.readString();
        this.phone = in.readString();
        this.time = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(line1);
        dest.writeString(area);
        dest.writeString(reference);
        dest.writeString(phone);
        dest.writeString(time);

    }

    public static final Parcelable.Creator<PersonalData> CREATOR = new Parcelable.Creator<PersonalData>(){

        public PersonalData createFromParcel(Parcel in) {
            return new PersonalData(in);
        }
        public PersonalData[] newArray(int size) {
            return new PersonalData[size];
        }
    };
}
