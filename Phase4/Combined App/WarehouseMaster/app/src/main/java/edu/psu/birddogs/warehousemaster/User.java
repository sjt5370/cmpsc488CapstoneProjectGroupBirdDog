package edu.psu.birddogs.warehousemaster;

public class User {
    private int acc_id;
    private int acc_type;
    private String username;
    private String password;
    private String first_name;
    private String last_name;
    private String job;
    private int productity;

    public User(){}

    public User(int acc_id, int acc_type, String username, String password, String first_name, String last_name, String job, int productity) {
        this.acc_id = acc_id;
        this.acc_type = acc_type;
        this.username = username;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.job = job;
        this.productity = productity;
    }

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

    public int getAcc_type() {
        return acc_type;
    }

    public void setAcc_type(int acc_type) {
        this.acc_type = acc_type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getProductity() {
        return productity;
    }

    public void setProductity(int productity) {
        this.productity = productity;
    }
}
