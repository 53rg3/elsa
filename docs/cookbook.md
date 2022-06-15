{:toc}



# Working with the test cluster

- `src/test/resources` contains Docker files

- You can run the cluster via:

  ```bash
  docker-compose up
  ```

- Open a shell into the container:

  ```bash
  docker container exec -it elasticsearch-node1 /bin/bash
  ```

  

