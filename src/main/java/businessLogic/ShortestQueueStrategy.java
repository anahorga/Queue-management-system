package businessLogic;

import model.Client;
import model.Coada;

import java.util.List;

public class ShortestQueueStrategy implements Strategy{
    @Override
    public void addTask(List<Coada> cozi, Client c) {
        int min=99999;
        int coadaMinima=0;
        int i=0;
        for(Coada co:cozi)
        {
            if(co.getClienti().size()<min)
            {
                min=co.getClienti().size();
                coadaMinima=i;
            }
            i++;
        }
        cozi.get(coadaMinima).addClient(c);
    }
}
