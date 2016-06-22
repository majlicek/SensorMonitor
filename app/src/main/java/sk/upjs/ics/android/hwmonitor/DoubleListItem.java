package sk.upjs.ics.android.hwmonitor;


/**
 * Created by chras on 20.06.2016.
 */
public class DoubleListItem {
    private String meno;
    private String hodnota;

    public DoubleListItem(String meno, float hodnota){
        this.meno = meno;
        if(hodnota == (long) hodnota)
            this.hodnota = String.format("%d",(long)hodnota);
        else
            this.hodnota =  String.format("%.2f",hodnota);
    }
    public DoubleListItem(String meno, String hodnota){
        this.meno = meno;
        this.hodnota  = hodnota;
    }

    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getHodnota() {
        return hodnota;
    }

    public void setHodnota(float hodnota) {
        if(hodnota == (long) hodnota)
            this.hodnota = String.format("%d",(long)hodnota);
        else
            this.hodnota =  String.format("%.2f",hodnota);
    }
    public void setHodnota(String hodnota) {
        this.hodnota = hodnota;
    }
}
