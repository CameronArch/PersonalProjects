with open("ALLFILLEDBoard.txt", "w") as file: 
    file.write("400\n400\n")

    for x in range(400): 
	for y in range(400):
	    file.write("{} {}\n".format(x,y))
