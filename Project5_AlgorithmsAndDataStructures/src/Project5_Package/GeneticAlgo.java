package Project5_Package;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;


public class GeneticAlgo {
	
	static ArrayList<Double> dataset;
	static double finalFitness = 0;
	
	
	class Individual{
		
		public ArrayList<Integer> chromosome = new ArrayList<Integer>();
		double fitness;
		
		public Individual(int n) {
			
			Set<Integer> chromosome = new LinkedHashSet<Integer>();//we use set to get unique number within the range
			
			Random r = new Random();
			
			while(chromosome.size()<n) {
				chromosome.add(r.nextInt(n));
			}
			
			this.chromosome = new ArrayList<Integer>(chromosome); //convert set into arrayList
			setFitness();
		}
		
		public void setFitness () {
			
			double res = 0;
			int size = chromosome.size();
			
			for(int i=0; i<size-1; i++) {
				int from = chromosome.get(i);
				int to = chromosome.get(i+1);
				
				//System.out.println(from+" "+to);
				//System.out.println(dataset[from][to]);
				
				res += dataset[from][to];
			}
			//System.out.println(ind.chromosome.get(size-1)+" "+ind.chromosome.get(0));
			res+= dataset[chromosome.get(size-1)][chromosome.get(0)]; //x[n][0] - return to the origin
			//System.out.println(res);
				
			this.fitness = res;
			
		}
		
		public Individual copyGenes(Individual ind) {
			
			Individual res = new Individual(ind.chromosome.size());
			
			for (int i=0; i<ind.chromosome.size(); i++) {
				res.chromosome.set(i, ind.chromosome.get(i));
			}
			
			return res;
		}
		
		public void printChromosome () {
			
			System.out.print(chromosome+" "+fitness);
		
		}
		
	}

	class Population{
		
		public ArrayList<Individual> population = new ArrayList<Individual>();
		
		public Population (int popSize, int chromosomeSize) {
		
			for (int i=0; i<popSize; i++) {
				Individual ind = new Individual(chromosomeSize);
				this.population.add(ind);
			}
		}
		
		public void printPop() {
			
			for (int i=0; i<population.size(); i++) {
				System.out.print(population.get(i).chromosome+"\t");
				System.out.println(population.get(i).fitness);
			}
		}
		
		public Individual crossOver (Individual p1, double rate) {
			
			Set<Integer> chromosome = new LinkedHashSet<Integer>();//we use set to get unique number within the range
			Individual res = new Individual(p1.chromosome.size());
			
			int point = (int)(p1.chromosome.size()*rate);
		
			for (int i=0; i<point; i++) {
				chromosome.add(p1.chromosome.get(i)); //copy some genes from the parent
			}
			
			Random r = new Random();
			while(chromosome.size()<p1.chromosome.size()) {
				chromosome.add(r.nextInt(p1.chromosome.size())); //get the remaining genes by random
			}
			
			res.chromosome = new ArrayList<Integer>(chromosome); //convert set into arrayList
			
			return res;
		}
		
		public Individual mutate (Individual p1) {
			Individual res = p1.copyGenes(p1); //we copy the parent's genes first
			
			//now we mutate the genes, using small change (swap genes by random)
			Random r = new Random();
			int i = r.nextInt(p1.chromosome.size());
			int j = r.nextInt(p1.chromosome.size());
			
			//to avoid getting the same gene
			while(i == j) {
				j = r.nextInt(p1.chromosome.size());
			}
			//System.out.println("i : "+i);
			//System.out.println("j : "+j);
			res.chromosome.set(i, p1.chromosome.get(j));
			res.chromosome.set(j, p1.chromosome.get(i));
			
			return res;
		}

		
	}
	
	public static void runGA() {
		
		//create a population object and parameters
		GeneticAlgo ga = new GeneticAlgo();
		//int chromosomeSize = 10;
		int numGeneration = 10;
		int popSize = 10;
		double crossOverRate = 0.6;
		
		//prepare dataset
		String file = "Weights.csv";
		dataset = Data.readFile(file);
		int chromosomeSize = dataset.size();
		//Data.printArray(dataset);
		
		//initialise the population
		Population pop = ga.new Population(popSize, chromosomeSize); //create 10 candidates, each candidates has 5 genes (5 nodes), pass dataset to calculate fitness
		
		//We sort the candidates by fitness in ascending order, the least the better in this example (TSP)
		Collections.sort(pop.population,new CompareFitness()); //sorting the population by fitness (asc)
		System.out.println("====Before Search====");
		pop.printPop();
		
		for (int gen=0; gen<numGeneration; gen++) {
			
			System.out.println("Generation : "+gen);
			
			//get the parents - top 2 from the list
			Individual p1 = pop.population.get(0);
			Individual p2 = pop.population.get(1);
			
			//get 2 new children
			Individual ch1 = pop.crossOver(p1, crossOverRate);
			Individual ch2 = pop.crossOver(p2, crossOverRate);
			
			//System.out.println("\nChild 1 : ");
			//ch1.printChromosome();
			//System.out.println("\nChild 2 : ");
			//ch2.printChromosome();
			
			//get a mutate child
			Individual ch3 = pop.mutate(p1);
			//System.out.println("\nChild 3 : ");
			//ch3.printChromosome();
			System.out.println();
			//add these new children to the population
			pop.population.add(ch1);
			pop.population.add(ch2);
			pop.population.add(ch3);
			
			//sort them
			Collections.sort(pop.population,new CompareFitness()); //sorting the population by fitness (asc)
			
			//remove the weak candidate
			//pop.population.remove(popSize-1);
			//pop.population.remove(popSize-1);
			//pop.population.remove(popSize-1);
			//pop.printPop();
			
		}
		
		System.out.println("====Result====");
		pop.printPop();
		
		String fileName = "/Users/zairulmazwan/git/tsp_genAlgo/TSP_GenAlgo/data/result.csv";
		Data.writeResult(fileName, pop);
		finalFitness = pop.population.get(0).fitness;
	
	}
	
	public static double calFitness (ArrayList<Integer> x) {
		double res = 0;
		int size = x.size();
		
		for(int i=0; i<size-1; i++) {
			int from = x.get(i);
			int to = x.get(i+1);
			
			//System.out.println(from+" "+to);
			//System.out.println(dataset[from][to]);
			
			res += dataset[from][to];
		}
		//System.out.println(ind.chromosome.get(size-1)+" "+ind.chromosome.get(0));
		res+= dataset[x.get(size-1)][x.get(0)]; //x[n][0] - return to the origin
		//System.out.println(res);
			
		return res;
	}

	public static void main(String[] args) {
		
		runGA();
		
		/*double [][] mst = MST.PrimsMST(dataset);
		double mstValue = MST.getMstValue(mst);
		System.out.println("\nMST fitness : "+MST.getMstValue(mst));
		DecimalFormat df = new DecimalFormat("#0.00");
		double resultQuality = (mstValue/finalFitness)*100;
		System.out.println("The solution is "+(df.format(resultQuality))+"% closer to the mst value.");
		//Data.printArray(mst);*/

	}

}