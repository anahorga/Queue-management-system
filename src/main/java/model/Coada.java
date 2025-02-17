package model;

import businessLogic.SimulationManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Coada implements Runnable{

    private BlockingQueue<Client> clienti;
    private AtomicInteger waitingPeriod;

    public Coada() {
        clienti=new LinkedBlockingQueue<>();
        waitingPeriod=new AtomicInteger(0);
    }
   public void addClient(Client clientNou)
   {
          clienti.add(clientNou);
          waitingPeriod.getAndAdd(clientNou.getServiceTime());


   }
    public BlockingQueue<Client> getClienti() {
        return clienti;
    }

    @Override
    public void run() {
      while(true)
      {
          try {
              Client cl = new Client();
              cl = clienti.peek();
              if (cl != null) {
                  if (cl.getServiceTime() != 0) {
                      
                          Thread.sleep(1000);
                          cl.setServiceTime(cl.getServiceTime() - 1);
                          waitingPeriod.decrementAndGet();

                  } else {
                          clienti.take();
                  }

              }
          }catch(InterruptedException e)
          {
              Thread.currentThread().interrupt();
          }
      }
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }
}
