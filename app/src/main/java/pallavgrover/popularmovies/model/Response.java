package pallavgrover.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Response {
        private int page;
        private int total_pages;
        private int total_results;

        private List<Reviews> results;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getTotal_pages() {
            return total_pages;
        }

        public void setTotal_pages(int total_pages) {
            this.total_pages = total_pages;
        }

        public int getTotal_results() {
            return total_results;
        }

        public void setTotal_results(int total_results) {
            this.total_results = total_results;
        }

        public List<Reviews> getResults() {
            return results;
        }

        public void setResults(List<Reviews> results) {
            this.results = results;
        }

        public class Reviews implements Parcelable{
            private String id;
            private String author;
            private String content;
            private String url;

            protected Reviews(Parcel in) {
                id = in.readString();
                author = in.readString();
                content = in.readString();
                url = in.readString();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(id);
                dest.writeString(author);
                dest.writeString(content);
                dest.writeString(url);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public final Creator<Reviews> CREATOR = new Creator<Reviews>() {
                @Override
                public Reviews createFromParcel(Parcel in) {
                    return new Reviews(in);
                }

                @Override
                public Reviews[] newArray(int size) {
                    return new Reviews[size];
                }
            };

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }