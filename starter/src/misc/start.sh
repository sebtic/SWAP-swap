#!/bin/bash
mkdir -p logs
java -jar swap-starter.jar -jars lib -main-class org.projectsforge.swap.proxy.starter.Main -output logs/log-$(date +%Y-%m-%d-%H-%M-%S).txt

