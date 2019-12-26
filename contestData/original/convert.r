#------------------------------
# Execute with: Rscript convert.r
# Used software: R version 3.2.2 (2015-08-14)
# https://www.r-project.org/
#------------------------------

print("Preparing contestdata values...")

voltage_factor <- 1
current_factor <- .01

voltage_data <- read.table("voltage_allrep.txt", sep = "" , header = F , na.strings ="", stringsAsFactors= F)[1]
current_data <- read.table("current.txt", sep = "" , header = F , na.strings ="", stringsAsFactors= F)

voltage_data <- voltage_data * voltage_factor
current_data <- current_data * current_factor

svg("voltage_all.svg")
matplot(voltage_data, lty=1, type="l")
svg("current_all.svg")
matplot(current_data, lty=1, type="l")

writePlotData <- function(folder, min_row, length, eachtimestep) {
    print(paste("Process folder:", folder))
    dir.create(folder)

    max_row <- min_row + length

    voltage_data <- voltage_data[min_row:max_row, ]
    current_data <- current_data[min_row:max_row, ]

    voltage_data <- voltage_data[seq(1, length(voltage_data), eachtimestep)]
    current_data <- current_data[seq(1, length(current_data), eachtimestep)]

    voltagefile = paste(folder, "/voltage.csv", sep="")
    inputfile = paste(folder, "/input.csv", sep="")


    write.table(voltage_data, file = voltagefile, row.names=FALSE, col.names=FALSE, sep="\n")
    write.table(current_data, file = inputfile, row.names=FALSE, col.names=FALSE, sep="\n")

    svg(paste(folder, "/voltage.svg", sep=""))
    matplot(voltage_data, lty=1, type="l")

    svg(paste(folder, "/input.svg", sep=""))
    matplot(current_data, lty=1, type="l")
}

## Old data sets
#writePlotData("../input-short", 186000, 16400, 1)
#writePlotData("../input-long", 186000, 47900, 1)

writePlotData("../input-calibration-01", 40000, 80000, 1)
writePlotData("../input-calibration-05", 40000, 80000, 5)
writePlotData("../input-real-01", 300000, 90000, 1)
writePlotData("../input-real-05", 300000, 90000, 5)


