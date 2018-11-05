FROM jupyter/base-notebook:latest

RUN pip install numpy Pillow matplotlib

ADD . /home/jovyan/work

CMD jupyter notebook --NotebookApp.token= --ip 0.0.0.0 --port 8888 /home/jovyan
