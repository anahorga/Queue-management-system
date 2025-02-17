package businessLogic;

import model.Client;
import model.Coada;

import java.util.List;

public class TimeStrategy implements Strategy{
    @Override
    public void addTask(List<Coada> cozi, Client c) {
        int min=99999;
        int coadaMinima=0;
        int i=0;

            for (Coada co : cozi) {
                int timpInFata = 0;
                for (Client cl : co.getClienti())
                    timpInFata += cl.getServiceTime();
                if (timpInFata < min) {
                    min = timpInFata;
                    coadaMinima = i;
                }
                i++;
            }
            cozi.get(coadaMinima).addClient(c);

    }
}
