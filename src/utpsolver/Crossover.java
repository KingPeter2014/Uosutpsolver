package utpsolver;

public class Crossover {
	private double crossover_probability=0.5,startpoint=0.5,endpoint=0.75;
	private int startCrossoverPoint=0,stopCrossoverPoint=0;
	private int [][] parent1,parent2,child1,child2;
	private int [] rooms,modules;
	int fosterRoom=0,fosterTime=0;
	private int timeslots=0,p1,p2; //p1 and p2 are the chromosome indices of selected parents for crossover
	private int [][][] chromosomes;
	
	
	public Crossover(int p1,int p2,int[][][] chromosomes,int numChromosome,int timeslots,int [] rooms,int[] modules, String[] moduleTypes, String[] roomTypes){
		this.chromosomes = chromosomes;
		this.p1=p1;
		this.p2=p2;
		this.startCrossoverPoint = (int)(timeslots * startpoint);
		this.stopCrossoverPoint = (int)(timeslots * endpoint);
		parent1 = new int[rooms.length][timeslots];
		parent2 = new int[rooms.length][timeslots];
		child1 = new int[rooms.length][timeslots];
		child2= new int[rooms.length][timeslots];
		this.rooms = rooms;
		this.modules = modules;
		this.timeslots = timeslots;
		this.createParents();
		this.initiaizeOffSpringToZero();
		this.createChildOne();
		this.createChildTwo();
		
		
	}
	
	public void createChildOne(){
		//Start by copying segment 25 percentile to 75th percentile to child1 from parent2
		for(int i=0;i < this.rooms.length;i++){
			for(int j=this.startCrossoverPoint;j< this.stopCrossoverPoint;j++ ){
				child1[i][j]= parent1[i][j];
			}
		}
		// Next copy the uncopied elements from chosen segment of parent2 into corresponding positions in child1
		for(int i=0;i < this.rooms.length;i++){
			for(int j=this.startCrossoverPoint;j< this.stopCrossoverPoint;j++ ){
				//check if each element in corresponding segment cell in parent2 has been copied
				if(parent2[i][j] !=0){
					boolean isNotCopied = this.elementNotCopied(2, parent2[i][j],this.startCrossoverPoint,this.stopCrossoverPoint);
					if(isNotCopied){
						//Check if the corresponding position in child one is free
						boolean isOccupied = this.correspondingPositionIsOccupied(1, i, j);
						if(!isOccupied){
							//Insert the gene here
							child1[i][j] = parent2[i][j];
						}
						else{
							//Get the position of child1[i][j] in parent2[i][j]
							this.getPositionOfChildElementInFosterParent(child1[i][j], 2);
							
							isOccupied = this.correspondingPositionIsOccupied(1, this.fosterRoom, this.fosterTime);
							//Insert parent2[i][j] into the found position in parent2[i][j] but into child1[][]
							if(isNotCopied && this.fosterRoom !=0 && this.fosterTime!=0){
								child1[this.fosterRoom][this.fosterTime] = parent2[i][j];
								this.fosterRoom=0;this.fosterTime=0;
							}
						}
						
					}
				}
			}
		}
		
	//Finally copy the other elements outside the chosen segment from parent 2 into corresponding positions in child1
		for(int i=0;i < this.rooms.length;i++){
			for(int j=0;j< timeslots;j++ ){
				//check if each element in corresponding  cell in parent2 has been copied into child1
				if(parent2[i][j] !=0){
					boolean isNotCopied = this.elementNotCopied(2, parent2[i][j],0,timeslots);
					if(isNotCopied){
						//Check if the corresponding position in child one is free
						boolean isOccupied = this.correspondingPositionIsOccupied(1, i, j);
						if(!isOccupied){
							//Insert the gene here
							child1[i][j] = parent2[i][j];
						}
						else{
							//Get the position of child1[i][j] in parent2[i][j],stored
							//in fosterRoom and fosterTime class attributes
							this.getPositionOfChildElementInFosterParent(child1[i][j], 2);
							isOccupied = this.correspondingPositionIsOccupied(1, this.fosterRoom, this.fosterTime);
							//Insert parent2[i][j] into the found position in parent2[i][j] but into child1[][]
							if(isNotCopied && this.fosterRoom !=0 && this.fosterTime!=0){
								child1[this.fosterRoom][this.fosterTime] = parent2[i][j];
								this.fosterRoom=0;this.fosterTime=0;
							}
						}
						
					}
				}
			}
		}
	}
	
	//Get the corresponding position in foster parent of an element in child that took position 
	//of which an element in the foster parent would have taken.
	private void getPositionOfChildElementInFosterParent(int childElement, int fosterParent){
		fosterRoom = fosterTime=0;
		switch(fosterParent){
		case 1:
			for(int i=0;i<this.rooms.length;i++){
				for(int j=0;j<timeslots;j++){
					//if(parent1[i][j]==childElement && (i <= this.startCrossoverPoint ||i >=this.stopCrossoverPoint)){
					if(parent1[i][j]==childElement){
						fosterRoom = i;fosterTime=j;
						return;
					}
						
				}
			}
			break;
		case 2:
			for(int i=0;i<this.rooms.length;i++){
				for(int j=0;j<timeslots;j++){
					//if(parent2[i][j]==childElement && (i <= this.startCrossoverPoint ||i >=this.stopCrossoverPoint)){
					if(parent2[i][j]==childElement){
						fosterRoom = i;fosterTime=j;
						return;
					}
						
				}
			}
			break;
		}
	}
	//Checks if a particular locus in a child chromosome is occupied or not
	private boolean correspondingPositionIsOccupied(int childToCheck, int room, int time){
		boolean isOccupied = false;
		switch(childToCheck){
		case 1:
			if(child1[room][time] !=0){
				isOccupied =true;
			}
			break;
		case 2:
			if(child2[room][time] !=0){
				isOccupied =true;
			}
			break;
			
		}//End Switch
		return isOccupied;
		
		
	}
	/*This method checks if an element from a segment in foster parent has been copied to segment in child
	that originated from a direct parent. child1 starts with a segment from parent1. 
	However, we need to check if all the elements from the corresponding segments originating
	from parent 2 has been copied to corresponding positions in the segment of child 1
	 * 
	 */
	private boolean elementNotCopied(int parent, int element,int startpos,int endpos){
		boolean notCopied = true;
		int countInChild=0,countInFosterParent=0;
		switch(parent){
		case 1://Checks if element from segment in parent 1 has been copied to segment in Child 2
			for(int i=0;i < this.rooms.length;i++){
				countInChild = this.countOccurrenceInChild(2, element);
				countInFosterParent = this.countInstanceOfModuleInParent(1, element);
				for(int j=startpos;j< endpos;j++ ){
					if(child2[i][j]==element && countInChild==countInFosterParent)
						//One may need to check how many times a module appeared in the parent to know
						//if finding one instance of it in child is enough to conclude that it has been copied
						return 	notCopied=false;
				}
			}
			break;
		case 2://Checks if element from segment in parent 2 has been copied to segment in Child 1
			countInChild = this.countOccurrenceInChild(1, element);
			countInFosterParent = this.countInstanceOfModuleInParent(2, element);
			for(int i=0;i < this.rooms.length;i++){
				for(int j=startpos;j< endpos;j++ ){
					if(child1[i][j]==element && countInChild==countInFosterParent)
						return 	notCopied=false;
				}
			}
			break;
			
		}//End Switch
		return notCopied;
	}
	//Check the number of instances of a module that has been copied to a child
	private int countOccurrenceInChild(int child, int module){
		int count=0;
		switch(child){
		case 1:
			for(int i=0;i < this.rooms.length;i++){
				for(int j=0;j<timeslots;j++){
					if(child1[i][j]==module)
						count+=1;
				}
			}

			break;
			
		case 2:
			for(int i=0;i < this.rooms.length;i++){
				for(int j=0;j<timeslots;j++){
					if(child2[i][j]==module)
						count+=1;
				}
			}
			break;
			
		}
		return count;
	}
	//Check the number of instances of a module that exist in a parent
	private int countInstanceOfModuleInParent(int parent, int module){
		int count=0;
		switch(parent){
		case 1:
			for(int i=0;i < this.rooms.length;i++){
				for(int j=0;j<timeslots;j++){
					if(parent1[i][j]==module)
						count+=1;
				}
			}

			break;
			
		case 2:
			for(int i=0;i < this.rooms.length;i++){
				for(int j=0;j<timeslots;j++){
					if(parent2[i][j]==module)
						count+=1;
				}
			}
			break;
			
		}
		return count;
	}

	private void createChildTwo(){
		//Start by copying segment 25th percentile to 75th percentile to child2 from parent1
		for(int i=0;i < this.rooms.length;i++){
			for(int j=this.startCrossoverPoint;j< this.timeslots;j++ ){
				child2[i][j]= parent2[i][j];
				}
		}
		// Next copy the uncopied elements from chosen segment of parent1 into corresponding positions in child2
		
				for(int i=0;i < this.rooms.length;i++){
					for(int j=this.startCrossoverPoint;j< this.timeslots;j++ ){
						//check if each element in corresponding segment cell in parent1 has been copied
						if(parent1[i][j] !=0){
							boolean isNotCopied = this.elementNotCopied(1, parent1[i][j],this.startCrossoverPoint,this.timeslots);
							if(isNotCopied){
								//Check if the corresponding position in child two is free
								boolean isOccupied = this.correspondingPositionIsOccupied(2, i, j);
								if(!isOccupied){
									//Insert the gene here
									child2[i][j] = parent1[i][j];
								}
								else{
									//Get the position of child2[i][j] in parent1[i][j]
									this.getPositionOfChildElementInFosterParent(child2[i][j], 1);
									
									isOccupied = this.correspondingPositionIsOccupied(2, this.fosterRoom, this.fosterTime);
									//Insert parent1[i][j] into the found position in parent1[i][j] but into child2[][]
									if(isNotCopied && this.fosterRoom !=0 && this.fosterTime!=0){
										child2[this.fosterRoom][this.fosterTime] = parent1[i][j];
										this.fosterRoom=0;this.fosterTime=0;
									}
								}
								
							}
						}
					}
				}
		
		
		//Finally copy the other elements outside the chosen segment from parent 1 into corresponding positions in child2
				for(int i=0;i < this.rooms.length;i++){
					for(int j=0;j< timeslots;j++ ){
						//check if each element in corresponding  cell in parent1 has been copied into child2
						if(parent2[i][j] !=0){
							boolean isNotCopied = this.elementNotCopied(1, parent1[i][j],0,timeslots);
							if(isNotCopied){
								//Check if the corresponding position in child two is free
								boolean isOccupied = this.correspondingPositionIsOccupied(2, i, j);
								if(!isOccupied){
									//Insert the gene here
									child2[i][j] = parent1[i][j];
								}
								else{
									//Get the position of child2[i][j] in parent1[i][j],stored
									//in fosterRoom and fosterTime class attributes
									this.getPositionOfChildElementInFosterParent(child2[i][j], 1);
									isOccupied = this.correspondingPositionIsOccupied(2, this.fosterRoom, this.fosterTime);
									//Insert parent1[i][j] into the found position in parent1[i][j] but into child2[][]
									if(isNotCopied && this.fosterRoom !=0 && this.fosterTime!=0){
										child2[this.fosterRoom][this.fosterTime] = parent1[i][j];
										this.fosterRoom=0;this.fosterTime=0;
									}
								}
								
							}
						}
					}
				}
		
	}
	
	private void compareOffspring(){
		
		
	}
	//Copy parents from the chromosome population to Parent1 and Parent 2
	private void createParents(){
		for(int i=0;i < this.rooms.length;i++){
			for(int j=0;j< this.timeslots;j++ ){
				parent1[i][j]= chromosomes[p1][i][j];
			}
		}
		for(int i=0;i < this.rooms.length;i++){
			for(int j=0;j< this.timeslots;j++ ){
				parent2[i][j]= chromosomes[p2][i][j];
			}
		}
	}
	//Sets default values of array elements of Child1 and Child2 to zero
	private void initiaizeOffSpringToZero(){
		for(int i=0;i < this.rooms.length;i++){
			for(int j=0;j< this.timeslots;j++ ){
				child1[i][j]= 0;
				child2[i][j]= 0;

			}
		}
		
	}
	public int[][] getFirstChild(){
		return this.child1;
	}
	public int[][] getSecondChild(){
		return this.child2;
	}
	public String printChildren(){
		
		String c = "<br/><hr/> Child1 after Crossover(PMX):<br/>";
		for(int b=0; b< Chromosomes.roomCount; b++){
			for(int a =0; a < timeslots; a++)
				c+=child1[b][a] + "&nbsp&nbsp&nbsp ";
			c+="<br/>";
			
		}
		c+="<br/><hr/> Child2 after Crossover(PMX):<br/>";
		for(int b=0; b< Chromosomes.roomCount; b++){
			for(int a =0; a < timeslots; a++)
				c+=child2[b][a] + "&nbsp&nbsp&nbsp ";
			c+="<br/>";
			
		}
		return c;
	}
	
}
