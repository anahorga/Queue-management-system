package businessLogic;

import model.Client;
import model.Coada;
import view.Interfata;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable{

    //date pe care le citim din interfata
    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int nrCozi;
    private int maxClienti;

    private String textDeAfisat="";
    public int nrClienti;
    public SelectionPolicy selectionPolicy;
    public int minArrivalTime;
    public int peekHour;
    public int maxArrivalTime;

    private Scheduler scheduler;
    private Interfata interfata;
    private List<Client> clientiGenerati;
    private double averageServiceTime;
    public AtomicInteger averageWaitingTime;


    public SimulationManager()
    {
        interfata   = new Interfata(this);
        averageWaitingTime=new AtomicInteger(0);
    }
    public void generarteNRandomClients()
    {
        clientiGenerati=new ArrayList<>();
        for(int i=0;i<nrClienti;i++)
        {
            Random random = new Random();
            // Generarea unui număr aleatoriu în intervalul specificat
            int timpSosire = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int timpInFata = random.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            Client c=new Client(i,timpSosire,timpInFata);
            clientiGenerati.add(c);
        }
        Collections.sort(clientiGenerati);
        averageServingTime();
    }

    void averageServingTime()
    {
        int cont=0;
        Iterator<Client> iterator = clientiGenerati.iterator();
        while (iterator.hasNext()) {
            Client cl = iterator.next();
            averageServiceTime+=cl.getServiceTime();
                    cont++;
        }
        averageServiceTime/=cont;
    }

    public synchronized void averageWaitingTimeMethod(Client c) {
        Coada coadaPotrivita=new Coada();
        for(Coada co:scheduler.getCoada()) {
            for(Client cl:co.getClienti())
                if(cl.getId()==c.getId()) {
                    coadaPotrivita = co;
                    break;
                }
        }
        AtomicInteger av=new AtomicInteger(0);
        for(Client cl:coadaPotrivita.getClienti())
        {
            if(cl.getId()==c.getId())
                break;
            else {
                av.getAndAdd(cl.getServiceTime()) ;
            }
        }
        averageWaitingTime.getAndAdd(av.intValue());
    }

    @Override
    public void run() {

        BufferedWriter writer = null;
        try {
            // Deschiderea fișierului pentru scriere
            writer = new BufferedWriter(new FileWriter("output.txt"));

            int currentTime = 0;
            while (currentTime < timeLimit) {

                Iterator<Client> iterator = clientiGenerati.iterator();
                while (iterator.hasNext()) {
                    Client cl = iterator.next();
                    if (cl.getArrivalTime() == currentTime) {
                        scheduler.dispatchTask(cl);
                        averageWaitingTimeMethod(cl);
                        iterator.remove(); // Elimină elementul folosind iteratorul
                    }
                }
                interfata.getAfisareArea().append(textDeAfisat);
                interfata.getAfisareArea().append("\n");
                textDeAfisat="";
                afisareCozi(currentTime,writer);

                currentTime++;
                synchronized (this) {
                    try {
                        this.wait(1000); // Așteaptă 1000 milisecunde (1 secundă)
                    } catch (InterruptedException e) {
                        // Tratează excepția dacă întreruperea thread-ului apare în timpul așteptării
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Scriere în fișier finalizată cu succes!");
        } catch (IOException e) {
            System.out.println("Eroare la scrierea în fișier: " + e.getMessage());
        } finally {
            try {
                // Închiderea fișierului
                if (writer != null) {
                    interfata.getAfisareArea().append(textDeAfisat);
                    interfata.getAfisareArea().append("\n");
                    writer.write("Peek Hour: "+String.valueOf(peekHour)+"\n");
                    writer.write("Average Service Time: "+String.valueOf(averageServiceTime)+"\n");
                    writer.write("Average Waiting Time: "+String.valueOf(averageWaitingTime.doubleValue()/nrClienti));
                    interfata.getAfisareArea().append("Peek Hour: "+String.valueOf(peekHour));
                    interfata.getAfisareArea().append("\n");
                    interfata.getAfisareArea().append("Average Service Time: "+String.valueOf(averageServiceTime));
                    interfata.getAfisareArea().append("\n");
                    interfata.getAfisareArea().append("Average Waiting Time: "+String.valueOf(averageWaitingTime.doubleValue()/nrClienti));
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Eroare la închiderea fișierului: " + e.getMessage());
            }
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void afisareCozi(int time, BufferedWriter writer)
    {
        try {
            // Scrie în fișier
            writer.newLine();
            textDeAfisat=textDeAfisat.concat("Time "+String.valueOf(time)+'\n');
            writer.write("Time "+time);
            writer.newLine();
            writer.write("Waiting Clients");
            textDeAfisat=textDeAfisat.concat("Waiting Clients \n");
            writer.newLine();
            for(Client cl: clientiGenerati)
            {
                writer.write(String.valueOf(cl));
                writer.write(';');
                textDeAfisat=textDeAfisat.concat(String.valueOf(cl)+';');
            }
            textDeAfisat=textDeAfisat.concat("\n");
            writer.newLine();
            int i=1;

            int catiOameni=0;
            for(Coada co:scheduler.getCoada())
            {

                catiOameni+=co.getClienti().size();
                writer.write("Queue "+i+':');
                textDeAfisat=textDeAfisat.concat("Queue "+String.valueOf(i)+':');
                if(co.getClienti().isEmpty()) {
                    writer.write("closed");
                    textDeAfisat=textDeAfisat.concat("closed");
                }
                else
                    for(Client cl:co.getClienti()) {
                        textDeAfisat=textDeAfisat.concat(String.valueOf(cl)+';');
                        writer.write(String.valueOf(cl));
                        writer.write(';');
                    }

                i++;
                textDeAfisat=textDeAfisat.concat("\n");
                writer.newLine();
            }

            if(maxClienti<catiOameni)
            {
                maxClienti=catiOameni;
                peekHour=time;
            }

        } catch (IOException e) {
            System.out.println("Eroare la scrierea în fișier: " + e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        SimulationManager gen=new SimulationManager();
    }
}
