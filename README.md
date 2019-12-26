# Neuron reconstruction

This is the implementation for my master thesis with the goal to reconstruct a neuron model with given membrane potential and current data.

## JDK

A compiler compliance level equal or greater than 1.8 is required to run the prototype.

## Used libraries

- [JCommon][JCommon] (GNU Lesser General Public Licence (LGPL) version 2.1 or later)
- [JFreeChart][JFreeChart] (GNU Lesser General Public Licence (LGPL))
- [MigLayout][MigLayout] (BSD or GPL license)

## Tests

### Generated data
You can find multiple tests for generated data, which should run without further configuration. Look into the package `de.hsmannheim.masterthesis.neuronreconstruction.tests.generateddata` for more details.

### Contest data
You can also find multiple tests which use measured data. The data isn't included and you have to create the project structure by yourself.
The data can be found [here][competition2009].
Be aware that I am not responsible for the data source and the data may be no longer available in the future.

Please put the data in the following folder structure in the current working directory: `contestData/original`. Then run the `convert.r` script which is present in this project structure to generate the input files.

After that you can run the tests in the package `de.hsmannheim.masterthesis.neuronreconstruction.tests.contestdata`.

## License
This software may be modified and distributed under the terms of the MIT license.  See the LICENSE file for details.


[JCommon]: http://www.jfree.org/jcommon/
[JFreeChart]: http://www.jfree.org/jfreechart/
[MigLayout]: http://www.miglayout.com/
[competition2009]: http://incf.org/community/competitions/archive/spike-time-prediction/2009  "Quantitative Single-Neuron Modeling 2009"
