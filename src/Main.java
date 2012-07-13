import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

class Main {
 public static String path = "/home/simon/" +
 		"projects/metodi_calcolo_scientifico/LuEig/matrice/";

 /*
  * Method that permit to read a file and 
  * create a Jama Matrix
  */
 public static Matrix readMatrix(String file) {
  Matrix matrix = null;
  Scanner sc;
  try {
   sc = new Scanner(new File(file));
   int size = sc.nextInt();
   matrix = new Matrix(size, size);

   for (int i = 0; i < size * size; i++) {
    int x = sc.nextInt();
    int y = sc.nextInt();
    double d = Double.parseDouble(sc.next());
    matrix.set(x - 1, y - 1, d);
   }

   sc.close();
  } catch (FileNotFoundException e) {
   e.printStackTrace();
  }

  return matrix;
 }

 /*
  * Method that permit to read a file and 
  * create a Jama Matrix with 1 columnn (an array)
  */
 public static Matrix readArray(String file) {
  Matrix matrix = null;
  Scanner sc;
  try {
   sc = new Scanner(new File(file));
   int size = sc.nextInt();
   matrix = new Matrix(size, 1);

   for (int i = 0; i < size; i++) {
    int x = sc.nextInt();
    double d = Double.parseDouble(sc.next());
    matrix.set(x - 1, 0, d);
   }

   sc.close();
  } catch (FileNotFoundException e) {
   e.printStackTrace();
  }

  return matrix;
 }

 /*
  * Method that permit to get all eigenvalues
  * ordered from a Jama Matrix
  */
 public static ArrayList<Double> getOrderedEigenValues(Matrix A) {
  double[][] values = A.getArray();
  ArrayList<Double> eigenValues = new ArrayList<Double>();

  for (int i = 0; i < values.length; i++) {
   eigenValues.add(values[i][i]);
  }

  Collections.sort(eigenValues);

  return eigenValues;
 }

 public static void main(String[] argv) {
  System.out
    .println("TEST        \t" +
    		"ERRORE RELATIVO \t" +
    		"ERRORE PRIMA COMP.\t" +
    		"AUTOVALORE NUMERO 7\t" +
    		"TIME TO SOLVE\t" +
    		"TIME TO EIGEN");
  System.out
    .println("------------\t" +
    		"----------------\t" +
    		"------------------\t" +
    		"-------------------\t" +
    		"-------------\t" +
    		"-------------");

  /* Name of all files */
  String[] names = { "easy-10", "easy-100", "easy-1000", "bad-10",
    "bad-100", "bad-500", "bad-1000", "verybad-10", "verybad-100",
    "verybad-500", "verybad-1000", "rand-10", "rand-100",
    "rand-1000", "rand-5000", "eig-10", "eig-20", "eig-30",
    "eig-40", "eig-50", "eig-100", "eig-1000", "eig-2000",
    "eig-5000" };

  String prefix = "matrice-";
  String postfix = ".dat";

  for (int i = 0; i < names.length; i++) {
   String nameFile = prefix + names[i] + postfix;
   String file = path + nameFile;
   Matrix A = readMatrix(file);

   String erroreRelativo = "n.a.";
   String errorePrimaComp = "n.a.";
   String settimoAutovalore = "n.a.";
   long inizioS = 0;
   long fineS = 0;
   long inizioE = 0;
   long fineE = 0;

   /* If the matrix isn't eig type than solve with lu decomposition */
   if (!nameFile.startsWith("matrice-eig")) {
    String fileNoti = file.replace("matrice", "terminenoto");
    Matrix b = readArray(fileNoti);
    int size = A.getArray().length;
    Matrix x_esatta = new Matrix(size, 1, 1.0);

    inizioS = new Date().getTime();
    Matrix x_calcolata = A.solve(b);
    fineS = new Date().getTime();
    Matrix diff = x_esatta.minus(x_calcolata);

    double erroreRelativoD = diff.normF() / x_esatta.normF();
    double errorePrimaCompD = Math.abs(x_calcolata.get(0, 0) - 1.0);

    erroreRelativo = String.format("%e", erroreRelativoD);
    errorePrimaComp = String.format("%e", errorePrimaCompD);
   }

   /* If the matrix isn't rand type than calculate eigenvalues */
   if (!nameFile.startsWith("matrice-rand")) {
    inizioE = new Date().getTime();
    EigenvalueDecomposition eg = new EigenvalueDecomposition(A);
    fineE = new Date().getTime();
    ArrayList<Double> eigenValues = getOrderedEigenValues(eg.getD());

    double settimoAutovaloreD = eigenValues.get(6);
    settimoAutovalore = String.format("%f", settimoAutovaloreD);
   }

   /* Print results */
   String printName = nameFile.replace("matrice-", "")
     .replace("-symm", "").replace(".dat", "");

   if (nameFile.startsWith("matrice-eig")) {
    System.out.printf("%12s\t%16s\t%18s\t%19s\t%12s\t\t%2dms%n",
      printName, erroreRelativo, errorePrimaComp,
      settimoAutovalore, "-", (fineE - inizioE));
   } else if (nameFile.startsWith("matrice-rand")) {
    System.out.printf("%12s\t%16s\t%18s\t%19s\t%2dms\t\t%12s%n",
      printName, erroreRelativo, errorePrimaComp,
      settimoAutovalore, (fineS - inizioS), "-");
   } else {
    System.out
      .printf("%12s\t%16s\t%18s\t%19s\t%2dms\t\t%2dms%n",
        printName, erroreRelativo, errorePrimaComp,
        settimoAutovalore, (fineS - inizioS),
        (fineE - inizioE));
   }
  }
 }
}