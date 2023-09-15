/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package restaurant;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author David
 */
public class Restaurant {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        
        
        Restaurant restaurante = new Restaurant();
        restaurante.initFirebase();
        Object opcion =restaurante.tomaDatos();
        restaurante.save(opcion);
        restaurante.recover();
        
    }
    private FirebaseDatabase firebaseDatabase;
    private  void initFirebase() {
        
        try {
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()   
                    .setDatabaseUrl("https://base2-73c02-default-rtdb.firebaseio.com")
                    //.setServiceAccount(new FileInputStream(new File("C:\\Users\\Lenovo\\Documents\\pc\\NetBeansProjects\\firebase\\prueba-esp-a7c2a-firebase-adminsdk-yd7qe-1bdb100458.json")))
                    .setServiceAccount(new FileInputStream(new File("C:\\Users\\David\\Documents\\NetBeansProjects\\Restaurant\\base2-73c02-firebase-adminsdk-fsvam-52f2178262.json")))

                   // .setDatabaseUrl("https://prueba-esp-a7c2a.firebaseio.com")
                    //.setServiceAccount(new FileInputStream(new File("C:\\Users\\Lenovo\\Documents\\pc\\NetBeansProjects\\firebase\\prueba-esp-a7c2a-firebase-adminsdk-yd7qe-1bdb100458.json")))
                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
            System.out.println("Conexión exitosa....");
        }catch (RuntimeException ex) {
            System.out.println("Problema ejecucion ");
        }catch (FileNotFoundException ex) {
            System.out.println("Problema archivo");
        }

         
    }
    private void save(Object opcion) throws FileNotFoundException {
        if (opcion != null) {
           // initFirebase();
            
            /* Get database root reference */
            DatabaseReference databaseReference = firebaseDatabase.getReference("/");
            
            /* Get existing child or will be created new child. */
            DatabaseReference childReference = databaseReference.child("platillo");

            /**
             * The Firebase Java client uses daemon threads, meaning it will not prevent a process from exiting.
             * So we'll wait(countDownLatch.await()) until firebase saves record. Then decrement `countDownLatch` value
             * using `countDownLatch.countDown()` and application will continues its execution.
             */
            CountDownLatch countDownLatch;
            countDownLatch = new CountDownLatch(1);
            childReference.setValue(opcion, (DatabaseError de, DatabaseReference dr) -> {
                System.out.println("Registro guardado!");
                // decrement countDownLatch value and application will be continues its execution.
                countDownLatch.countDown();
            });
            try {
                //wait for firebase to saves record.
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static String capturarDatos(String mensaje){
        Scanner scanner = new Scanner(System.in);
        System.out.println(mensaje);
        String tomadeDatos;
        tomadeDatos = scanner.nextLine();
        
        return tomadeDatos;
    }
    private void recover() {
        
            //initFirebase();

            
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("platillo");

// Agrega un ValueEventListener para escuchar cambios en los datos
        databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
        // Este método se llama cuando los datos cambian en Firebase
        // dataSnapshot contiene los datos que recuperaste

        

        // O para recuperar una lista de objetos
        List<Object> objetos = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Object objeto = snapshot.getValue(Object.class);
            objetos.add(objeto);
        }

        System.out.print("la mesa "+objetos.set(0, objetos)+" pidio "+objetos.get(1)+" y debe cancelar "+objetos.get(2));
        }   

        @Override
        public void onCancelled(DatabaseError databaseError) {
        // Este método se llama si hay un error al recuperar los datos
        // Maneja el error aquí si es necesario
        }
        });
    }
    public Object tomaDatos(){
        String dato = capturarDatos("Que deseas comer? tebemos como opciones hamburguesa, pizza o salchipapas");
        Menu opcion = new Menu();
        
        
        int contadorMesa =opcion.getMesa();
        contadorMesa++;
        opcion.setMesa(contadorMesa);
        opcion.setName(dato);
        switch(dato){
            case "hamburguesa" -> opcion.setPrecio(18900);
            case "pizza" -> opcion.setPrecio(15000);
            case "salchipapas" -> opcion.setPrecio(12000);
        }
        
        return opcion;
        
    }
}

