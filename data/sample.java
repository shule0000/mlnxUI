Patient patient= new Patient(); // fake patient
for (int indexC = 0; indexC<8; ++indexC)
	patient.setChannelFlag(indexC);
DataBufferInterface dataBufferInstance;
dataBufferInstance = new MultiChannelBufferTestRealData(patient);


dataBufferInstance.start();
dataBufferInstance...

