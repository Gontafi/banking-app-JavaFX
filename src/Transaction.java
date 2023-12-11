import java.util.Date;
import java.util.Objects;

public class Transaction {
    public Double amount;
    public String from;
    public String to;
    public String description;

    public Date time;
    public Transaction(Double amount, String from, String to, String description, Date time) {
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.description = description;
        this.time = time;
    }


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDescription() {
        if (Objects.equals(description, "Перевод")) {
            return time.toString() + " " + description + " " + from + " на " + to + " на сумму " + amount + "$";
        }
        return time.toString() + " " + description + " " + amount + "$";
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
