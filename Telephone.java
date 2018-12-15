import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.text.*;
public class Telephone implements Serializable{

    private boolean deleted;
    private String name;
    private String surname;
    private String patronymic;
    private String number;
    private int rate;
    private String date;
    private double discount;
    private String talkingStartTime;
    private String talkingEndTime;

    public Telephone(String name, String patronymic,String surname, String number, int rate){
        this.name=name;
        this.surname=surname;
        this.patronymic=patronymic;
        this.number=number;
        this.rate=rate;
        this.date=DateFormat.getDateInstance().format(new Date());
        this.discount=Math.random()*100 -100;
        this.talkingStartTime =DateFormat.getTimeInstance().format(new Date());
        this.talkingEndTime =DateFormat.getTimeInstance().format(new Date());
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String toString() {
        return (this.name +" "+ this.patronymic +" " + this.surname+ ", telephone number : " + this.number + ", rate : " + this.rate + ", " + this.date);
    }

    public Object getFieldName(String fieldName)  throws IllegalAccessException, NoSuchFieldException {
        Field field = this.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(this);
    }

    public void setDeleted(){
        this.deleted=true;
    }



}

