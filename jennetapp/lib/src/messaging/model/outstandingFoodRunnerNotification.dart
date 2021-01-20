class OutstandingFoodRuneerNotification
{
  String foodRunnerId;
  String startTime;
  String estimatedTimeOfInterval;

  OutstandingFoodRuneerNotification();

  String getFoodRunnerId() 
  {
        return foodRunnerId;
  }

  void setFoodRunnerId(String foodRunnerId) 
  {
        this.foodRunnerId = foodRunnerId;
  }

  String getStartTime() 
  {
        return startTime;
  }

  void setStartTime(String startTime) 
  {
        this.startTime = startTime;
  }

  String getEstimatedTimeOfArrival() 
  {
    return estimatedTimeOfInterval;
  }

  setEstimatedTimeOfArrival(String estimatedTimeOfInterval) 
  {
        this.estimatedTimeOfInterval = estimatedTimeOfInterval;
  }
}