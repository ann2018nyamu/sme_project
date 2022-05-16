package ke.co.eclectic.quickstore.models.others;

public class MyApiResponses<T,Z,Y> {
    private Throwable error;
    private String operation;
    private String status;
    private Y y;



    /**
     * Instantiates a new My api responses.
     *
     * @param operation the operation performed
     * @param status    the status of operation
     * @param y         the resulting object from operation
     */
    public MyApiResponses( String operation, String status,Y y) {
        this.y = y;
        this.operation = operation;
        this.status = status;
    }

    /**
     * Instantiates a new My api responses.
     *
     * @param operation the operation performed
     * @param status    the status of operation
     * @param obj       the resulting error object from operation
     */
    public MyApiResponses( String operation, String status,Throwable obj) {
        this.error = obj;
        this.operation = operation;
        this.status = status;
    }

    /**
     * Gets object.
     *
     * @return the object
     */
    public Y getObject() {
        return y;
    }

    /**
     * Gets error.
     *
     * @return the error
     */
    public Throwable getError() {
        return error;
    }

    /**
     * Gets operation.
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }


}
