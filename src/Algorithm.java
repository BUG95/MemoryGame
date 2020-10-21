import java.util.Random;
class Algorithm {
     int[] generateVectorWithUniqueNumbers(int size){
        int[] array = new int[size];
        Random r = new Random();
        array[0] = r.nextInt(size);
        for(int i = 1; i < size; i++){
            array[i] = r.nextInt(size);
            while(isNotUnique(array[i], array, i))
                array[i] = r.nextInt(size);
        }
        return array;
    }

    private boolean isNotUnique(int number, int []array, int limit){
        for(int i = 0; i < limit; i++)
            if(array[i] == number) return true;
        return false;
    }
}
