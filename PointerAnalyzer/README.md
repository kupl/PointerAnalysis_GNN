# Running Pointer Analysis with classification results



## Running porinter analysis with your node classification result

(1) Save your classification result in 'classification.fact' file

(2) Type the following command:

```sh
python run_new.py GNN <pgm>
```
where <pgm> is a target program to be analyzed (e.g., 'luindex').
```
antlr, eclipsem, chart, fop, xalan, bloat, lusearch, hsqldb, jython, luindex,
batik, checkstyle, sunflow, findbugs, jpc,luindex, antlr, pmd, xalan09,
eclipse, chart, fop, xalan, bloat, jedit, briss, lusearch, hsqldb, jython
```
For example, the following command will analyze the program 'luindex':
```sh
python run_new.py GNN luindex
```

## Running baseline analysis 

  
(1) Type the following command to produce classification result of the baseline:
  
```sh
python run_new.py zipper <pgm>
```
The result will be saved in 'output-zipper' folder

  
(2) Copy the classification result into classification.facts by typing the following command:
  
```sh
cp output-zipper/<pgm>-ZipperPrecisionCriticalMethod.facts classification.facts
```

  
(3) Run analysis with the following command:
  
```sh
python run_new.py Baseline <pgm>
```


