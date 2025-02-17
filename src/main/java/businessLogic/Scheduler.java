package businessLogic;

import model.Client;
import model.Coada;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private List<Coada> cozi;
    private int nrMaxCozi;
    private Strategy strategy;

    public Scheduler(int nrMaxCozi) {
        cozi=new ArrayList<>();
        this.nrMaxCozi=nrMaxCozi;
           for(int i=0;i<nrMaxCozi;i++)
           {
               Coada coada=new Coada();
               cozi.add(coada);
               Thread t=new Thread(coada);
               t.start();

           }

    }

    public void changeStrategy(SelectionPolicy policy)
    {
        if(policy==SelectionPolicy.SHORTEST_TIME){
            strategy=new TimeStrategy();
        }
        if(policy==SelectionPolicy.SHORTEST_QUEUE){
            strategy=new ShortestQueueStrategy();
        }
    }
    public void dispatchTask(Client c)
    {
        strategy.addTask(cozi,c);

    }
    public List<Coada> getCoada()
    {
        return cozi;
    }
}
