package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Vote_view{
    private int votes;

    Vote_view() {
        this.votes = 0;
    }

    synchronized int getVotes(){
        return this.votes;
    }

    private synchronized String deposit(int amount) {
        this.votes += amount;
        return "successfully deposited " + amount + " to the votes.";
    }

    private synchronized String withdrawal(int amount) {
        if (votes >= amount) {
            votes -= amount;
        }
        else{
            return "Insufficient votes for a withdrawal of " + amount + " from the candidate.";
        }
        return "successfully withdrawed " + amount + " votes from the candidate.";

    }

}

class vote_choise extends Thread{


    public synchronized void run() {
        try{ Thread.sleep(7);
        }
        catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Thread name: " + Thread.currentThread().getName());
        }

}


public class Main {
    public static void main(String[] args) {
        System.out.println("Testcomm 2!");


        Thread[] threads = new Thread[5];

        System.out.println("--------Executables--------");
        Vote_view acc = new Vote_view();

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 5; i++) {
            threads[i] = new vote_choise();
        }

        for (int i = 0; i < 5; i++) {
            executor.execute(threads[i]);
        }
    }

}