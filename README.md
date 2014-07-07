[![Build Status](https://travis-ci.org/orwell-int/proxy-robots.svg?branch=master)](https://travis-ci.org/orwell-int/proxy-robots) [![Stories in Ready](https://badge.waffle.io/orwell-int/proxy-robots.png?label=ready&title=Ready)](https://waffle.io/orwell-int/proxy-robots) [![Coverage Status](https://img.shields.io/coveralls/orwell-int/proxy-robots.svg)](https://coveralls.io/r/orwell-int/proxy-robots)
proxy-robots
============

Handles the communication between the server and the (real) robots.

Running it
----------

Show the existing targets:
```
ant -p
```

Build, upload and run the program on the robot:
```
ant uploadandrun_robots
```

Build the proxy:
```
ant compile_proxy
```

Run the proxy:
```
ant run_proxy 
```

Run the junit test:
```
ant junit-proxy -v
```

Build a test report
```
ant junitreport 
```
