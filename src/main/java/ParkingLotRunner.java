import static org.junit.Assert.*;

import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.junit.*;
import org.junit.runner.*;
import org.apache.commons.lang3.tuple.*;
import org.json.simple.JSONObject;
import com.google.common.util.concurrent.ListenableFuture;

public class ParkingLotRunner {

class ParkingLot {
    private int FLOOR_COUNT=3;
    private int ROW_COUNT=1;
    private int COLUMN_COUNT=1;
    private int [][][] allocation;
    private int [] floorType;
    private int allocationCount;
  
      public ParkingLot() {
        allocation=new int[FLOOR_COUNT][ROW_COUNT][COLUMN_COUNT];
        for(int i=0;i<FLOOR_COUNT;++i){
          for(int j=0;j<ROW_COUNT;++j){
            for(int k=0;k<COLUMN_COUNT;++k){
              allocation[i][j][k]=-1;
            }
          }
        }
        floorType=new int[FLOOR_COUNT];
        for(int i=0;i<FLOOR_COUNT;++i){
          floorType[i]=i%3+1;
        }
        allocationCount=0;
      }
    
      private boolean canFit(int vehicleType, int floor){
        return floorType[floor] >= vehicleType; 
      }
    
      private boolean tryBookingSpot(int floor, 
          int vehicleType, int vehicleNumber){
        if(!canFit(vehicleType, floor))return false;
        for(int i=0;i<ROW_COUNT;++i){
          for(int j=0;j<COLUMN_COUNT;++j){
             if(allocation[floor][i][j]==-1){
               allocation[floor][i][j]=vehicleNumber;
               return true;
             }
          }
        }
        return false;
      }
    
      public boolean bookSpot(int vehicleType, int vehicleNumber) {
         for(int i=0;i<FLOOR_COUNT;++i){
            if(tryBookingSpot(i, vehicleType, vehicleNumber)){
              ++allocationCount;
              return true; 
            }
         }
         return false;
      }
    
      public boolean checkOut(int vehicleType, int vehicleNumber){
        for(int floor=0;floor<FLOOR_COUNT;++floor){
          for(int i=0;i<ROW_COUNT;++i){
            for(int j=0;j<COLUMN_COUNT;++j){
               if(allocation[floor][i][j]==vehicleNumber){
                 allocation[floor][i][j]=-1;
                 --allocationCount;
                 return true;
               }
            }
          }
        }
        return false;
      }
    
      public int countEmptySpots() {
        return FLOOR_COUNT*ROW_COUNT*COLUMN_COUNT-allocationCount;
      }
    
      public boolean isEmpty() {
        return allocationCount==0;
      }
    
      public boolean isFull() {
        return allocationCount==FLOOR_COUNT*ROW_COUNT*COLUMN_COUNT;
      }
  }
  @Test
  public void testNoop() {
    assertEquals(parkingLot.isEmpty(),true);
    assertEquals(parkingLot.bookSpot(3,1),true);
    assertEquals(parkingLot.bookSpot(3,2),false);
    assertEquals(parkingLot.countEmptySpots(),2);
    assertEquals(parkingLot.bookSpot(1,3),true);
    assertEquals(parkingLot.bookSpot(1,4),true);
    assertEquals(parkingLot.isFull(),true);
    assertEquals(parkingLot.countEmptySpots(),0);
  }
  
  private ParkingLot parkingLot;
  
  @Before
  public void setUp(){
    parkingLot=new ParkingLot();
  }

  public static void main(String[] args) {
    JUnitCore.main("ParkingLotRunner");
  }

}