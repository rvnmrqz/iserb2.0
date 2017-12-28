package sticaloocanteam.i_serb;

/**
 * Created by kim on 12/2/2017.
 */

public class Object_SearchResults {

    private String skill_id;
    private String user_icon;
    private String user_id;
    private String worker_name;
    private String skill_description;
    private String service_fee;
    private String rating;

    public Object_SearchResults(String skill_id, String user_icon, String user_id, String worker_name, String skill_description, String service_fee, String rating) {
        this.skill_id = skill_id;
        this.user_icon = user_icon;
        this.user_id = user_id;
        this.worker_name = worker_name;
        this.skill_description = skill_description;
        this.service_fee = service_fee;
        this.rating = rating;
    }

    public String getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(String skill_id) {
        this.skill_id = skill_id;
    }

    public String getUser_icon() {
        return user_icon;
    }

    public void setUser_icon(String user_icon) {
        this.user_icon = user_icon;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWorker_name() {
        return worker_name;
    }

    public void setWorker_name(String worker_name) {
        this.worker_name = worker_name;
    }

    public String getSkill_description() {
        return skill_description;
    }

    public void setSkill_description(String skill_description) {
        this.skill_description = skill_description;
    }

    public String getService_fee() {
        return service_fee;
    }

    public void setService_fee(String service_fee) {
        this.service_fee = service_fee;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
