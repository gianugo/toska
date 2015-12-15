A 13 years old project from Sourceforge that I'm bringing over to 
Github for archival and posterity. Not much to see here, I doubt
it even works nowadays.

Toska
=====

Introduction
------------
This is a little description of the toska package. At least to get you going. 
Any questions, please feel free to contact me at nito@qindel.es

The purpose of this package is well explained in the main web page, it is mainly
a Java interface which allows you to deploy ssh keys across several boxes.

Prerequisites: Knowing how the public and private key mechanism in ssh works and
               be familiar with modifying the location of the private/public keys

Functionality
-------------
At the moment this package has only been tested with OpenSSH keys (which are the
ones that I use). The current functionality permits to do the following:

1) Define hosts where the keys are going to be distributed.
   Be aware that if you are running toska as a different user than the host where
   you are trying to deploy the keys it might be a good idea to define the host
   as user@host (for example root@remotehost or even better sshman@remotehost).
2) Define the remote users for each of  the hosts (mentioned in 1))
3) Define public keys that will be used as authentication tokens for the remote
   users (as defined in 2))
4) Deploy all the keys in a directory structure with the following format:
   remotehost1/remoteUser1
   remotehost1/remoteUser2
   ...
   remotehost1/remoteUsern
   ...
   remotehostn/remoteUser1
   remotehostn/remoteUser2
   ...
   remotehostn/remoteUsern

   where each of the remotehostn is a directory and each of the remoteUsern is a
   file which contents are the public keys of the users that you want to distribute.

5) Distribute the keys
   The keys get all distributed for each host in the same target directory

   Note: to permit to use such an authentication you need to use the 
         "AuthorizedKeysFile      /etc/ssh/keys/%u"
         option in the sshd_config file.

From version 1.0 you will also be able to define aliases for the following elements:
i) hosts. A host alias works in the following way, it will expand all the elements of
   alias until no further expansion is possible.

   All the non expanded elements are considered hosts. An example.

   Assume that the following hostAlias exists:

   hostAlias1: hostAlias2, hostAlias3, host1
   hostAlias2: hostAlias1, host2, host3
   hostAlias3: host4, host5

   The expansion of hostAlias1 would be:
   hostAlias1: host1, host2, host3, host4, host5
   hostAlias2: host1, host2, host3, host4, host5
   hostAlias3: host4, host5

   The algorithm is to expand each of the elements of the alias that is an alias until
   the number of elements remains the same across two iterations. Then all the
   elements identified as aliases are removed. If a string that cannot be identified
   as an alias is found then it is assumed to be a host.

ii) Users. The user alias is defined in the same way as a host Alias.

iii) Keys. The algorithm is the same as the hosts one with the following differences:

   a) If the expansion of an key Alias generates some key elements which haven't been
      defined then no error is generated, the public key will simply not be generated.

