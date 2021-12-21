package Project5_Package;

public class CompareFitness implements java.util.Comparator {
	
	GeneticAlgo ga = new GeneticAlgo();
	
	public int compare(Object a, Object b) 
	{
		if (((GeneticAlgo.Individual)a).fitness < ((GeneticAlgo.Individual)b).fitness) return(-1);
		if (((GeneticAlgo.Individual)a).fitness > ((GeneticAlgo.Individual)b).fitness) return(1);
		return(0);
	}

}
