package com.fsdeveloper.jobmanager.bean;

/**
 *  The Job class represents all objects of type Job.
 *  All objects of type Job are implemented as instances of this class.
 *
 * @author Created by Douglas Rafael on 20/04/2016.
 * @version 1.1
 */
public class Job {
    private int id;
    private String title;
    private String description;
    private String note;
    private Double price;
    private Double expense;
    private String finalized_at;
    private String created_at;
    private String updated_at;
    private User user;
    private Client client;

    /**
     * Job class constructor.
     *
     * @param id The id of job.
     * @param title The title of job.
     * @param description The description of job.
     * @param note The note of job.
     * @param price The price of job.
     * @param expense The value of the expenditure spent for performing the job.
     * @param finalized_at The date and time that was finalized.
     * @param created_at The date and time that was created.
     * @param user The user who made the job.
     * @param client The client that requested the job.
     */
    public Job(int id, String title, String description, String note, Double price, Double expense, String finalized_at, String created_at, User user, Client client) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.note = note;
        this.price = price;
        this.expense = expense;
        this.finalized_at = finalized_at;
        this.created_at = created_at;
        this.user = user;
        this.client = client;
    }

    /**
     * Retrieves/get the id of job.
     *
     * @return The id of job.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id of job.
     *
     * @param id The id of job.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves/get title of job.
     *
     * @return The title of job.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title of job.
     *
     * @param title The title job.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves/get description of job.
     *
     * @return The description of job.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description of job.
     *
     * @param description The description of job.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves/get note of job.
     *
     * @return The note of job.
     */
    public String getNote() {
        return note;
    }

    /**
     * Set the note of job.
     *
     * @param note The note of job.
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Retrieves/get price of job.
     *
     * @return The note of job.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Set the price of job.
     *
     * @param price The price of job.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Retrieves/get expense of job.
     *
     * @return The expense of job.
     */
    public Double getExpense() {
        return expense;
    }

    /**
     * Set the expense of job.
     *
     * @param expense The expense of job.
     */
    public void setExpense(Double expense) {
        this.expense = expense;
    }

    /**
     * Retrieves/get the date and time the job was finalized.
     *
     * @return The date and time.
     */
    public String getFinalized_at() {
        return finalized_at;
    }

    /**
     * Set the date and time the job was finalized.
     *
     * @param finalized_at The date and time.
     */
    public void setFinalized_at(String finalized_at) {
        this.finalized_at = finalized_at;
    }

    /**
     * Retrieves/get the date and time the job was created.
     *
     * @return The date and time.
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * Set the date and time the job was created.
     *
     * @param created_at The date and time.
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * Retrieves/get the date and time the job was updated.
     *
     * @return The date and time.
     */
    public String getUpdated_at() {
        return updated_at;
    }

    /**
     * Set the date and time the job was updated.
     *
     * @param updated_at The date and time.
     */
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    /**
     * Retrieves/get the user who made the job.
     *
     * @return The user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Set the user who made the job.
     *
     * @param user The user.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Retrieves/get the client The client that requested the job.
     *
     * @return The client.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Set the client The client that requested the job.
     *
     * @param client The client.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", note='" + note + '\'' +
                ", price=" + price +
                ", expense=" + expense +
                ", finalized_at='" + finalized_at + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", user=" + user +
                ", client=" + client +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Job job = (Job) o;

        return (id == job.id && title.equals(job.title) && user.equals(job.user) && client.equals(job.client));
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        return result;
    }
}

