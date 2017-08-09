package pallavgrover.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class VideosBean {

        private List<Trailer> results;

        public List<Trailer> getResults() {
            return results;
        }

        public void setResults(List<Trailer> results) {
            this.results = results;
        }

        public class Trailer implements Parcelable{
            private String id;
            private String iso_639_1;
            private String iso_3166_1;
            private String key;
            private String name;
            private String site;
            private int size;
            private String type;

            protected Trailer(Parcel in) {
                id = in.readString();
                iso_639_1 = in.readString();
                iso_3166_1 = in.readString();
                key = in.readString();
                name = in.readString();
                site = in.readString();
                size = in.readInt();
                type = in.readString();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(id);
                dest.writeString(iso_639_1);
                dest.writeString(iso_3166_1);
                dest.writeString(key);
                dest.writeString(name);
                dest.writeString(site);
                dest.writeInt(size);
                dest.writeString(type);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public final Creator<Trailer> CREATOR = new Creator<Trailer>() {
                @Override
                public Trailer createFromParcel(Parcel in) {
                    return new Trailer(in);
                }

                @Override
                public Trailer[] newArray(int size) {
                    return new Trailer[size];
                }
            };

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIso_639_1() {
                return iso_639_1;
            }

            public void setIso_639_1(String iso_639_1) {
                this.iso_639_1 = iso_639_1;
            }

            public String getIso_3166_1() {
                return iso_3166_1;
            }

            public void setIso_3166_1(String iso_3166_1) {
                this.iso_3166_1 = iso_3166_1;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSite() {
                return site;
            }

            public void setSite(String site) {
                this.site = site;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
