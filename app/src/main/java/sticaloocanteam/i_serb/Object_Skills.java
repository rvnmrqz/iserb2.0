package sticaloocanteam.i_serb;


public class Object_Skills {

    private String skill_id;
    private String iconURL;
    private String skill_name;
    private String service_fee;
    private double rating;
    private int numberOfReviews;


    public Object_Skills(String skill_id, String iconURL, String skill_name, String service_fee, double rating, int numberOfReviews) {
        this.skill_id = skill_id;
        this.iconURL = iconURL;
        this.skill_name = skill_name;
        this.service_fee = service_fee;
        this.rating = rating;
        this.numberOfReviews = numberOfReviews;
    }

    public String getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(String skill_id) {
        this.skill_id = skill_id;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getSkill_name() {
        return skill_name;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }

    public String getService_fee() {
        return service_fee;
    }

    public void setService_fee(String service_fee) {
        this.service_fee = service_fee;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }




}
