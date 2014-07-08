# -*- mode: ruby -*-
# vi: set ft=ruby :

# USAGE:
# 1) Set the following variables in your .bashrc:
#   GIT_REMOTE_STRING using the http format:
#   http://username:password@githostname/organisation/secrets-repo.git
# and GIT_SECRET_PASSPHRASE containing the secret-password
#
# source ~/.bashrc
# vagrant plugin install ansible
# vagrant plugin install vagrant-serverspec
# Make sure you have a local NFS server running
# vagrant up
# vagrat provision

Vagrant.require_plugin "ansible"
Vagrant.require_plugin "vagrant-serverspec"

Vagrant.configure("2") do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "centos-6.4-ansible"

  # The url from where the 'config.vm.box' box will be fetched if it
  # doesn't already exist on the user's system.
  config.vm.box_url = "http://boxes.pixelcookers.com/centos-6.4-ansible.box"

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # config.vm.network :forwarded_port, guest: 8080, host: 8080
  config.vm.network :forwarded_port, guest: 9000, host: 9001


  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  config.vm.network :private_network, ip: "10.11.12.14"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network :public_network

  # If true, then any SSH connections made will enable agent forwarding.
  # Default value: false
  # config.ssh.forward_agent = true

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  #config.vm.synced_folder "ansible", "/home/ansible"

  config.ssh.forward_agent = true

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  config.vm.provider :virtualbox do |vb|
  #   # Don't boot with headless mode
  #   vb.gui = true

    # Use VBoxManage to customize the VM. For example to change memory:
    vb.customize ["modifyvm", :id, "--memory", "4096"]
    vb.customize ["modifyvm", :id, "--cpus", 4]

    # dns issue
    vb.customize ["modifyvm", :id, "--natdnsproxy1", "off"]
    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
  end
  #
  # View the documentation for the provider you're using for more
  # information on available options.

  #config.vm.provision :shell, :path => "ansible-bootstrap.sh", :keepcolor => true
  config.vm.provision :shell, :inline => '(rpm -qa | grep epel-release) || rpm -i http://mirrors.coreix.net/fedora-epel/6/i386/epel-release-6-8.noarch.rpm && yum install ansible -y'
  config.vm.provision :shell, :inline => '(rpm -qa | grep nfs-utils) || yum install nfs-utils -y'
  config.vm.provision :shell, :inline => '(rpm -qa | grep portmap) || yum install portmap -y'

  config.vm.provision :ansible do |ansible|
    ansible.verbose = "v"
    #ansible.tags = "sass"
    ansible.playbook = "ansible/playbook.yml"
    ansible.extra_vars = {
      GIT_REMOTE_STRING: ENV['GIT_REMOTE_STRING'],
      GIT_SECRET_PASSPHRASE: ENV['GIT_SECRET_PASSPHRASE']
    }
  end

  config.vm.provision :serverspec do |spec|
    spec.pattern = '*_spec.rb'
  end
end
